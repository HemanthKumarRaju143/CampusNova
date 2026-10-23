package com.KumarCollege.Service.Impl;

import com.KumarCollege.Entity.Program;
import com.KumarCollege.Enum.ProgramStatus;
import com.KumarCollege.Enum.StudentEnrollmentStatus;
import com.KumarCollege.Enum.StudentStatus;

import com.KumarCollege.Pojo.Course.Req.CreateProgramReq;
import com.KumarCollege.Pojo.Course.Req.ReplaceProgramRequest;
import com.KumarCollege.Pojo.Course.Response.ProgramResponse;
import com.KumarCollege.Pojo.Course.Response.ProgramStudentResponse;
import com.KumarCollege.Pojo.Course.Response.ProgramStudentsResponse;
import com.KumarCollege.Repository.DepartmentRepository;
import com.KumarCollege.Repository.DegreeRepository;
import com.KumarCollege.Repository.ProgramRepository;
import com.KumarCollege.Repository.StudentClassEnrollmentRepository;

import com.KumarCollege.Service.ProgramService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramServiceImpl implements ProgramService {

    private final ProgramRepository programRepository;
    private final DepartmentRepository departmentRepository;
    private final DegreeRepository degreeRepository;

    // CHANGED: Retained the JOIN-based student lookup repository.
    private final StudentClassEnrollmentRepository enrollmentRepository;

    @Override
    public List<ProgramResponse> createProgram(List<CreateProgramReq> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one program is required"
            );
        }

        // CHANGED: The unique key is degreeId + programCode.
        Set<String> uniqueProgramKeys = new HashSet<>();

        // Validate all codes before saving any programs.
        for (CreateProgramReq request : requests) {

            String code = normalizeCode(request.getProgramCode());
            Long degreeId = request.getDegreeId();

            String key = degreeId + ":" + code;

            if (!uniqueProgramKeys.add(key)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Duplicate program code for degree ID: " + degreeId
                                + ", code: " + code
                );
            }

            if (programRepository
                    .existsByDegreeIdAndProgramCodeIgnoreCase(degreeId, code)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Program code already exists for degree ID: "
                                + degreeId + ", code: " + code
                );
            }
        }

        List<Program> programs = new ArrayList<>();

        for (CreateProgramReq request : requests) {

            // CHANGED: Validate the department foreign key.
            departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Department not found: " + request.getDepartmentId()
                    ));

            // CHANGED: Validate the degree foreign key.
            degreeRepository.findById(request.getDegreeId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Degree not found: " + request.getDegreeId()
                    ));

            Program program = new Program();

            program.setDepartmentId(request.getDepartmentId());
            program.setDegreeId(request.getDegreeId());
            program.setProgramCode(normalizeCode(request.getProgramCode()));
            program.setProgramName(request.getProgramName().trim());
            program.setStatus(ProgramStatus.ACTIVE);

            programs.add(program);
        }

        return programRepository.saveAll(programs)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProgramResponse> getAllPrograms(int page, int size) {

        validatePagination(page, size);

        Pageable pageable = PageRequest.of(page, size);

        // CHANGED: Retrieve active programs rather than active courses.
        return programRepository
                .findByStatus(ProgramStatus.ACTIVE, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramResponse getProgramById(Long programId) {

        return toResponse(findProgram(programId));
    }

    @Override
    public ProgramResponse updateProgram(
            Long programId,
            ReplaceProgramRequest request) {

        Program program = findProgram(programId);

        String code = normalizeCode(request.getProgramCode());
        String name = request.getProgramName().trim();

        // CHANGED: Check uniqueness within the selected degree,
        // while excluding this program's own ID.
        if (programRepository
                .existsByDegreeIdAndProgramCodeIgnoreCaseAndProgramIdNot(
                        request.getDegreeId(), code, programId)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Program code already exists for this degree"
            );
        }

        // CHANGED: Validate the new department.
        departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Department not found: " + request.getDepartmentId()
                ));

        // CHANGED: Validate the new degree.
        degreeRepository.findById(request.getDegreeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Degree not found: " + request.getDegreeId()
                ));

        program.setDepartmentId(request.getDepartmentId());
        program.setDegreeId(request.getDegreeId());
        program.setProgramCode(code);
        program.setProgramName(name);
        program.setStatus(request.getStatus());

        return toResponse(programRepository.save(program));
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramStudentsResponse getProgramStudents(
            Long programId,
            StudentStatus studentStatus,
            StudentEnrollmentStatus enrollmentStatus) {

        // CHANGED: Check whether the program exists.
        Program program = findProgram(programId);

        String studentStatusValue =
                studentStatus == null ? null : studentStatus.name();

        String enrollmentStatusValue =
                enrollmentStatus == null ? null : enrollmentStatus.name();

        // CHANGED: The repository query must filter by programId,
        // joining classes through classes.program_id.
        List<Object[]> rows = enrollmentRepository.findProgramStudents(
                programId,
                studentStatusValue,
                enrollmentStatusValue
        );

        ProgramStudentsResponse response = new ProgramStudentsResponse();
        response.setProgramId(program.getProgramId());
        response.setProgramName(program.getProgramName());

        List<ProgramStudentResponse> students = new ArrayList<>();

        for (Object[] row : rows) {

            /*
             * Expected row order:
             * 0 = programId
             * 1 = programName
             * 2 = studentId
             * 3 = studentName
             * 4 = admissionNumber
             * 5 = email
             * 6 = section
             * 7 = studentStatus
             * 8 = enrollmentStatus
             *
             * CHANGED: These indexes must match the SELECT order
             * in findProgramStudents().
             */

            ProgramStudentResponse student = new ProgramStudentResponse();

            student.setStudentId(((Number) row[2]).longValue());
            student.setStudentName((String) row[3]);
            student.setAdmissionNumber((String) row[4]);
            student.setEmail((String) row[5]);
            student.setSection((String) row[6]);

            student.setStudentStatus(
                    StudentStatus.valueOf(row[7].toString())
            );

            student.setEnrollmentStatus(
                    StudentEnrollmentStatus.valueOf(row[8].toString())
            );

            students.add(student);
        }

        response.setStudents(students);

        return response;
    }

    private Program findProgram(Long programId) {

        if (programId == null || programId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Program ID must be positive"
            );
        }

        return programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Program not found with ID: " + programId
                ));
    }

    private String normalizeCode(String code) {

        if (code == null || code.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Program code is required"
            );
        }

        return code.trim().toUpperCase();
    }

    private void validatePagination(int page, int size) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be 0 or greater"
            );
        }

        if (size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page size must be between 1 and 100"
            );
        }
    }

    private ProgramResponse toResponse(Program program) {

        ProgramResponse response = new ProgramResponse();

        response.setProgramId(program.getProgramId());
        response.setDepartmentId(program.getDepartmentId());
        response.setDegreeId(program.getDegreeId());
        response.setProgramCode(program.getProgramCode());
        response.setProgramName(program.getProgramName());
        response.setStatus(program.getStatus());

        return response;
    }
}