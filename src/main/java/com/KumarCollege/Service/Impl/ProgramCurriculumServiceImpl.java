package com.KumarCollege.Service.Impl;

import com.KumarCollege.Entity.ProgramCurriculum;
import com.KumarCollege.Pojo.ProgramCurriculum.Req.ChangeMandatoryRequest;
import com.KumarCollege.Pojo.ProgramCurriculum.Req.CreateCurriculumRequest;
import com.KumarCollege.Pojo.ProgramCurriculum.Response.ProgramCurriculumResponse;
import com.KumarCollege.Repository.ProgramCurriculumRepository;
import com.KumarCollege.Repository.ProgramRepository;
import com.KumarCollege.Repository.SubjectRepository;
import com.KumarCollege.Service.ProgramCurriculumService;
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
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgramCurriculumServiceImpl implements ProgramCurriculumService {

    private final ProgramCurriculumRepository curriculumRepository;
    private final ProgramRepository programRepository;
    private final SubjectRepository subjectRepository;

    // CREATE: supports one or multiple records through one method.
    @Override
    public List<ProgramCurriculumResponse> create(
            List<CreateCurriculumRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one curriculum record is required"
            );
        }

        Set<String> uniqueKeys = new HashSet<>();

        // Validate every request before saving anything.
        for (CreateCurriculumRequest request : requests) {

            if (request == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Curriculum request cannot be null"
                );
            }

            validateReferences(request.getProgramId(),
                    request.getSubjectId());

            validateSemester(request.getSemester());

            String key = request.getProgramId() + "-"
                    + request.getSubjectId() + "-"
                    + request.getSemester();

            if (!uniqueKeys.add(key)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Duplicate program, subject and semester in request"
                );
            }

            if (curriculumRepository
                    .existsByProgramIdAndSubjectIdAndSemester(
                            request.getProgramId(),
                            request.getSubjectId(),
                            request.getSemester())) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "This subject is already assigned to this program "
                                + "for the specified semester"
                );
            }
        }

        List<ProgramCurriculum> entities = new ArrayList<>();

        for (CreateCurriculumRequest request : requests) {
            ProgramCurriculum entity = new ProgramCurriculum();

            entity.setProgramId(request.getProgramId());
            entity.setSubjectId(request.getSubjectId());
            entity.setSemester(request.getSemester());
            entity.setMandatory(request.getIsMandatory());

            entities.add(entity);
        }

        return curriculumRepository.saveAll(entities)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // READ: pagination + optional filters.
    @Override
    @Transactional(readOnly = true)
    public Page<ProgramCurriculumResponse> getAll(
            Long programId,
            Long subjectId,
            Byte semester,
            Boolean isMandatory,
            int page,
            int size) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be >= 0 and size must be between 1 and 100"
            );
        }

        if (programId != null && programId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Program ID must be positive");
        }

        if (subjectId != null && subjectId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Subject ID must be positive");
        }

        if (semester != null) {
            validateSemester(semester);
        }

        Pageable pageable = PageRequest.of(page, size);

        return curriculumRepository.searchCurriculum(
                        programId,
                        subjectId,
                        semester,
                        isMandatory,
                        pageable)
                .map(this::toResponse);
    }

    // READ: one record.
    @Override
    @Transactional(readOnly = true)
    public ProgramCurriculumResponse getById(Long id) {
        return toResponse(findById(id));
    }

    // UPDATE: replace the editable fields of one record.
    @Override
    public ProgramCurriculumResponse replace(
            Long id,
            CreateCurriculumRequest request) {

        ProgramCurriculum existing = findById(id);

        validateReferences(request.getProgramId(),
                request.getSubjectId());

        validateSemester(request.getSemester());

        boolean duplicate =
                curriculumRepository
                        .existsByProgramIdAndSubjectIdAndSemester(
                                request.getProgramId(),
                                request.getSubjectId(),
                                request.getSemester());

        boolean sameCombination =
                existing.getProgramId().equals(request.getProgramId())
                        && existing.getSubjectId().equals(request.getSubjectId())
                        && existing.getSemester().equals(request.getSemester());

        if (duplicate && !sameCombination) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This subject is already assigned to this program "
                            + "for the specified semester"
            );
        }

        existing.setProgramId(request.getProgramId());
        existing.setSubjectId(request.getSubjectId());
        existing.setSemester(request.getSemester());
        existing.setMandatory(request.getIsMandatory());

        return toResponse(curriculumRepository.saveAndFlush(existing));
    }

    // PATCH: update only mandatory/elective status.
    @Override
    public ProgramCurriculumResponse changeMandatory(
            Long id,
            ChangeMandatoryRequest request) {

        ProgramCurriculum existing = findById(id);

        existing.setMandatory(request.getIsMandatory());

        return toResponse(curriculumRepository.saveAndFlush(existing));
    }

    private ProgramCurriculum findById(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Curriculum ID must be positive"
            );
        }

        return curriculumRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Curriculum record not found: " + id
                ));
    }

    private void validateReferences(Long programId, Long subjectId) {
        if (!programRepository.existsById(programId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Program not found: " + programId
            );
        }

        if (!subjectRepository.existsById(subjectId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Subject not found: " + subjectId
            );
        }
    }

    private void validateSemester(Byte semester) {
        if (semester == null || semester < 1 || semester > 12) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Semester must be between 1 and 12"
            );
        }
    }

    private ProgramCurriculumResponse toResponse(ProgramCurriculum entity) {
        ProgramCurriculumResponse response = new ProgramCurriculumResponse();

        response.setCurriculumId(entity.getCurriculumId());
        response.setProgramId(entity.getProgramId());
        response.setSubjectId(entity.getSubjectId());
        response.setSemester(entity.getSemester());
        response.setIsMandatory(entity.getMandatory());

        return response;
    }
}