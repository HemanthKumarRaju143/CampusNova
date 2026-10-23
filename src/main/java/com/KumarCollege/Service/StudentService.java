package com.KumarCollege.Service;

import com.KumarCollege.Enum.StudentStatus;
import com.KumarCollege.Pojo.Student.Req.ChangeStatusRequest;
import com.KumarCollege.Pojo.Student.Req.CreateStudentRequest;
import com.KumarCollege.Pojo.Student.Req.ReplaceStudentRequest;
import com.KumarCollege.Pojo.Student.Req.TransferStudentRequest;
import com.KumarCollege.Pojo.Student.Response.StudentResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StudentService {
    public List<StudentResponse> createStudent(List<CreateStudentRequest> requests);

    public Page<StudentResponse> getAllStudents(StudentStatus status, int page , int size);

    public StudentResponse getById(Long id);

    public StudentResponse replaceRecords(Long id , ReplaceStudentRequest request);

    public StudentResponse changeStudentStatus(Long id , ChangeStatusRequest request);

    public StudentResponse transferStudent(Long id , TransferStudentRequest request);
}
