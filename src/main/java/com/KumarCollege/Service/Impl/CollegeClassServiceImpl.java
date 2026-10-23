package com.KumarCollege.Service.Impl;

import com.KumarCollege.Entity.CollegeClass;
import com.KumarCollege.Enum.ClassStatus;
import com.KumarCollege.Pojo.Class.Req.ChangeClassStatusRequest;
import com.KumarCollege.Pojo.Class.Req.CreateClassRequest;
import com.KumarCollege.Pojo.Class.Req.ReplaceRecordRequest;
import com.KumarCollege.Pojo.Class.Response.ClassResponse;
import com.KumarCollege.Repository.CollegeClassRepository;
import com.KumarCollege.Service.ClassService;
import com.KumarCollege.Validation.ClassValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CollegeClassServiceImpl implements ClassService {

    private final CollegeClassRepository collegeClassRepository;
    private final ClassValidation validation;

    @Override
    @Transactional
    public List<ClassResponse> createClass(
            List<CreateClassRequest> requests) {

        // Validate every program before saving any class.
        for (CreateClassRequest request : requests) {
            validation.validateProgram(request.getProgramId());
        }

        List<CollegeClass> classes = new ArrayList<>();

        for (CreateClassRequest request : requests) {

            CollegeClass collegeClass = new CollegeClass();

            // CHANGED: courseId -> programId
            collegeClass.setProgramId(request.getProgramId());

            collegeClass.setClassName(request.getClassName().trim());

            collegeClass.setAcademicYear(
                    request.getAcademicYear().trim()
            );

            // CHANGED: Save semester from the request.
            collegeClass.setSemester(request.getSemester());

            collegeClass.setSection(
                    normalizeSection(request.getSection())
            );

            collegeClass.setStatus(ClassStatus.ACTIVE);

            classes.add(collegeClass);
        }

        List<CollegeClass> savedClasses =
                collegeClassRepository.saveAll(classes);

        return savedClasses.stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClassResponse> getAllClasses(
            ClassStatus status,
            int page,
            int size) {

        validation.validatePagination(page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<CollegeClass> classes;

        if (status == null) {
            classes = collegeClassRepository.findAll(pageable);
        } else {
            classes = collegeClassRepository.findByStatus(
                    status, pageable
            );
        }

        return classes.map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassResponse getClassById(Long id) {

        CollegeClass collegeClass = validation.findClass(id);

        return convertToResponse(collegeClass);
    }

    @Override
    @Transactional
    public ClassResponse updateClass(
            Long id,
            ReplaceRecordRequest request) {

        CollegeClass collegeClass = validation.findClass(id);

        // CHANGED: Validate the destination program.
        validation.validateProgram(request.getProgramId());

        // CHANGED: Update programId instead of courseId.
        collegeClass.setProgramId(request.getProgramId());

        collegeClass.setClassName(request.getClassName().trim());

        collegeClass.setAcademicYear(
                request.getAcademicYear().trim()
        );

        // CHANGED: Update semester.
        collegeClass.setSemester(request.getSemester());

        collegeClass.setSection(
                normalizeSection(request.getSection())
        );

        CollegeClass savedClass =
                collegeClassRepository.save(collegeClass);

        return convertToResponse(savedClass);
    }

    @Override
    @Transactional
    public ClassResponse changeClassStatus(
            Long id,
            ChangeClassStatusRequest request) {

        CollegeClass collegeClass = validation.findClass(id);

        collegeClass.setStatus(request.getStatus());

        CollegeClass savedClass =
                collegeClassRepository.save(collegeClass);

        return convertToResponse(savedClass);
    }

    // CHANGED: Course-based filtering -> program-based filtering.
    @Override
    @Transactional(readOnly = true)
    public Page<ClassResponse> getClassesByProgram(
            Long programId,
            ClassStatus status,
            int page,
            int size) {

        validation.validatePagination(page, size);
        validation.validateProgram(programId);

        Pageable pageable = PageRequest.of(page, size);

        Page<CollegeClass> classes;

        if (status == null) {
            classes = collegeClassRepository.findByProgramId(
                    programId, pageable
            );
        } else {
            classes = collegeClassRepository.findByProgramIdAndStatus(
                    programId, status, pageable
            );
        }

        return classes.map(this::convertToResponse);
    }

    private String normalizeSection(String section) {

        if (section == null || section.isBlank()) {
            return null;
        }

        return section.trim().toUpperCase(Locale.ROOT);
    }

    private ClassResponse convertToResponse(
            CollegeClass collegeClass) {

        ClassResponse response = new ClassResponse();

        response.setClassId(collegeClass.getClassId());

        // CHANGED: Return programId.
        response.setProgramId(collegeClass.getProgramId());

        response.setClassName(collegeClass.getClassName());
        response.setAcademicYear(collegeClass.getAcademicYear());

        // CHANGED: Return semester.
        response.setSemester(collegeClass.getSemester());

        response.setSection(collegeClass.getSection());
        response.setStatus(collegeClass.getStatus());

        response.setCreatedAt(collegeClass.getCreatedAt());
        response.setUpdatedAt(collegeClass.getUpdatedAt());

        return response;
    }
}
