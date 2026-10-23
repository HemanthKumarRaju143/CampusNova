package com.KumarCollege.Validation;

import com.KumarCollege.Entity.Student;
import com.KumarCollege.Repository.DepartmentRepository;
import com.KumarCollege.Repository.ProgramRepository;
import com.KumarCollege.Repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class StudentValidation {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final ProgramRepository programRepository;

    public void existsByAdmissionNumber(String admissionNumber){
        if(studentRepository.existsByAdmissionNumberIgnoreCase(admissionNumber)){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Admission number already exists: " + admissionNumber);
        }
    }

    public void notExistsByDepartmentId(Long id){
        if(!departmentRepository.existsById(id)){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Department not found: " + id);
        }
    }

    public void notExistByCourseId(Long id){
        if(!programRepository.existsById(id)){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Course not found: " + id);
        }
    }

    public Student getStudentById(Long id){

        return studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student not found with ID: " + id
                ));
    }

    public void isAdmissionNumberAndStudentIdExists(String admissionNumber , Long id){
        if (studentRepository
                .existsByAdmissionNumberIgnoreCaseAndStudentIdNot(
                        admissionNumber, id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Admission number already belongs to another student"
            );
        }
    }
}
