package com.KumarCollege.Service.Impl;

import com.KumarCollege.Entity.Student;
import com.KumarCollege.Enum.StudentStatus;
import com.KumarCollege.Pojo.Student.Req.ChangeStatusRequest;
import com.KumarCollege.Pojo.Student.Req.CreateStudentRequest;
import com.KumarCollege.Pojo.Student.Req.ReplaceStudentRequest;
import com.KumarCollege.Pojo.Student.Req.TransferStudentRequest;
import com.KumarCollege.Pojo.Student.Response.StudentResponse;
import com.KumarCollege.Repository.ProgramRepository;
import com.KumarCollege.Repository.StudentRepository;
import com.KumarCollege.Service.StudentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    // CHANGED: Removed DepartmentRepository.
    // A student references a program, and a program references its department.
    private final ProgramRepository programRepository;

    @Override
    public List<StudentResponse> createStudent(List<CreateStudentRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one student is required"
            );
        }

        Set<String> admissionNumbers = new HashSet<>();

        // First pass: validate the entire request before saving.
        for (CreateStudentRequest request : requests) {

            if (request == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Student request cannot be null"
                );
            }

            String admissionNumber = request.getAdmissionNumber()
                    .trim()
                    .toUpperCase(Locale.ROOT);

            // CHANGED: Detect duplicates within the same bulk request.
            if (!admissionNumbers.add(admissionNumber)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Duplicate admission number in request: " + admissionNumber
                );
            }

            // CHANGED: Validate the program instead of department/course separately.
            validateProgramExists(request.getProgramId());

            if (studentRepository.existsByAdmissionNumberIgnoreCase(admissionNumber)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Admission number already exists: " + admissionNumber
                );
            }
        }

        // Second pass: build entities only after validation.
        List<Student> students = new ArrayList<>();

        for (CreateStudentRequest request : requests) {
            Student student = new Student();

            student.setProgramId(request.getProgramId()); // CHANGED
            student.setAdmissionNumber(
                    request.getAdmissionNumber().trim().toUpperCase(Locale.ROOT)
            );
            student.setFirstName(request.getFirstName().trim());
            student.setLastName(
                    request.getLastName() == null ? null : request.getLastName().trim()
            );
            student.setEmail(request.getEmail().trim());
            student.setMobile(request.getMobile().trim());
            student.setDateOfBirth(request.getDateOfBirth());
            student.setAdmissionYear(request.getAdmissionYear());
            student.setStatus(StudentStatus.ACTIVE);

            students.add(student);
        }

        List<Student> savedStudents = studentRepository.saveAll(students);

        return savedStudents.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponse> getAllStudents(
            StudentStatus status,
            int page,
            int size
    ) {
        validatePage(page, size);

        Pageable pageable = PageRequest.of(page, size);

        // CHANGED: A null status returns all students instead of filtering by ACTIVE.
        Page<Student> students = (status == null)
                ? studentRepository.findAll(pageable)
                : studentRepository.findByStatus(status, pageable);

        return students.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getById(Long id) {
        return convertToResponse(findStudent(id));
    }

    @Override
    public StudentResponse replaceRecords(Long id, ReplaceStudentRequest request) {

        Student student = findStudent(id);

        validateProgramExists(request.getProgramId());

        String admissionNumber = request.getAdmissionNumber()
                .trim()
                .toUpperCase(Locale.ROOT);

        // CHANGED: Check uniqueness while excluding this student's own ID.
        if (studentRepository.existsByAdmissionNumberIgnoreCaseAndStudentIdNot(
                admissionNumber, id
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Admission number already exists: " + admissionNumber
            );
        }

        // CHANGED: Removed the old incorrect check that rejected a replacement
        // merely because the status was unchanged.

        student.setProgramId(request.getProgramId());
        student.setAdmissionNumber(admissionNumber);
        student.setFirstName(request.getFirstName().trim());
        student.setLastName(
                request.getLastName() == null ? null : request.getLastName().trim()
        );
        student.setEmail(request.getEmail().trim());
        student.setMobile(request.getMobile().trim());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setAdmissionYear(request.getAdmissionYear());
        student.setStatus(request.getStatus());

        // Updates the existing student row; it does not create another student.
        return convertToResponse(studentRepository.saveAndFlush(student));
    }

    @Override
    public StudentResponse changeStudentStatus(
            Long id,
            ChangeStatusRequest request
    ) {
        Student student = findStudent(id);

        if (student.getStatus() == request.getStatus()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Student already has status: " + request.getStatus()
            );
        }

        student.setStatus(request.getStatus());

        return convertToResponse(studentRepository.saveAndFlush(student));
    }

    @Override
    @Transactional
    public StudentResponse transferStudent(
            Long id,
            TransferStudentRequest request
    ) {
        // 1. Find the existing CSE admission record.
        Student oldStudent = findStudent(id);

        // 2. Only an active admission can be transferred.
        if (oldStudent.getStatus() != StudentStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only active students can be transferred"
            );
        }

        // 3. Validate the destination program (for example, EEE).
        validateProgramExists(request.getNewProgramId());

        // 4. The destination must differ from the current program.
        if (oldStudent.getProgramId().equals(request.getNewProgramId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Student is already assigned to this program"
            );
        }

        // 5. Normalize the new admission number.
        String newAdmissionNumber = request.getNewAdmissionNumber()
                .trim()
                .toUpperCase(Locale.ROOT);

        // 6. Reject reuse of an existing admission number.
        if (studentRepository.existsByAdmissionNumberIgnoreCase(
                newAdmissionNumber)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Admission number already exists: " + newAdmissionNumber
            );
        }

        // 7. IMPORTANT: Deactivate the old CSE record.
        oldStudent.setStatus(StudentStatus.INACTIVE);

        // CHANGED: Save the old record before creating the new one.
        // Both operations still belong to the same transaction.
        studentRepository.saveAndFlush(oldStudent);

        // 8. Create a NEW admission record for the EEE program.
        Student newStudent = new Student();

        newStudent.setProgramId(request.getNewProgramId());
        newStudent.setAdmissionNumber(newAdmissionNumber);

        // Copy the student's personal details.
        newStudent.setFirstName(oldStudent.getFirstName());
        newStudent.setLastName(oldStudent.getLastName());
        newStudent.setEmail(oldStudent.getEmail());
        newStudent.setMobile(oldStudent.getMobile());
        newStudent.setDateOfBirth(oldStudent.getDateOfBirth());

        // Set new admission details.
        newStudent.setAdmissionYear(request.getNewAdmissionYear());
        newStudent.setStatus(StudentStatus.ACTIVE);

        // 9. Save the new record; MySQL generates a new student_id.
        Student savedNewStudent = studentRepository.saveAndFlush(newStudent);

        // 10. Return the new ACTIVE EEE admission record.
        return convertToResponse(savedNewStudent);
    }

    private void validateProgramExists(Long programId) {
        if (programId == null || programId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Program ID must be positive"
            );
        }

        if (!programRepository.existsById(programId)) {
            throw new EntityNotFoundException(
                    "Program not found with ID: " + programId
            );
        }
    }

    private Student findStudent(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Student ID must be positive"
            );
        }

        return studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Student not found with ID: " + id
                ));
    }

    private void validatePage(int page, int size) {
        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size must be between 1 and 100"
            );
        }
    }

    private StudentResponse convertToResponse(Student student) {
        StudentResponse response = new StudentResponse();

        response.setStudentId(student.getStudentId());
        response.setAdmissionNumber(student.getAdmissionNumber());
        response.setFirstName(student.getFirstName());
        response.setLastName(student.getLastName());
        response.setEmail(student.getEmail());
        response.setMobile(student.getMobile());
        response.setDateOfBirth(student.getDateOfBirth());
        response.setAdmissionYear(student.getAdmissionYear());
        response.setStatus(student.getStatus());

        return response;
    }
}