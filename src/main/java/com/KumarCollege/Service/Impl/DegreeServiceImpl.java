package com.KumarCollege.Service.Impl;

import com.KumarCollege.Entity.Degree;
import com.KumarCollege.Enum.DegreeStatus;
import com.KumarCollege.Pojo.Degree.Request.CreateDegreeRequest;
import com.KumarCollege.Pojo.Degree.Response.DegreeResponse;
import com.KumarCollege.Pojo.Degree.Request.UpdateDegreeDurationRequest;
import com.KumarCollege.Repository.DegreeRepository;
import com.KumarCollege.Service.DegreeService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DegreeServiceImpl implements DegreeService {

    private final DegreeRepository degreeRepository;

    @Override
    @Transactional
    public List<DegreeResponse> createBulkDegrees(
            List<CreateDegreeRequest> requests) {

        List<Degree> degrees = new ArrayList<>();

        // Step 1: Convert each request DTO into a Degree entity.
        for (CreateDegreeRequest request : requests) {
            Degree degree = new Degree();

            degree.setDegreeCode(request.getDegreeCode());
            degree.setDegreeName(request.getDegreeName());
            degree.setLevel(request.getStatus());
            degree.setDurationYears(request.getDurationYears());

            degrees.add(degree);
        }

        // Step 2: Save all entities.
        List<Degree> savedDegrees = degreeRepository.saveAll(degrees);

        // Step 3: Convert saved entities into response DTOs.
        List<DegreeResponse> responses = new ArrayList<>();

        for (Degree degree : savedDegrees) {
            DegreeResponse response = new DegreeResponse();

            response.setDegreeId(degree.getDegreeId());
            response.setDegreeCode(degree.getDegreeCode());
            response.setDegreeName(degree.getDegreeName());
            response.setLevel(degree.getLevel());
            response.setDurationYears(degree.getDurationYears());

            responses.add(response);
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public DegreeResponse getDegreeById(Long id) {

        return toResponse(findDegree(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DegreeResponse> getAllDegrees(
            int page,
            int size,
            String sortBy,
            String direction) {

        validatePagination(page, size);

        if (sortBy == null || sortBy.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Sort field is required"
            );
        }

        Sort.Direction sortDirection;

        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Direction must be ASC or DESC"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        return degreeRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    public DegreeResponse updateDegree(
            Long id,
            CreateDegreeRequest request) {

        Degree degree = findDegree(id);

        String code = normalizeCode(request.getDegreeCode());
        String name = request.getDegreeName().trim();

        if (degreeRepository.existsByDegreeCodeIgnoreCaseAndDegreeIdNot(
                code, id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Degree code is already used by another degree"
            );
        }

        if (degreeRepository.existsByDegreeNameIgnoreCaseAndDegreeIdNot(
                name, id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Degree name is already used by another degree"
            );
        }

        degree.setDegreeCode(code);
        degree.setDegreeName(name);
        degree.setLevel(request.getStatus());
        degree.setDurationYears(request.getDurationYears());

        return toResponse(degreeRepository.save(degree));
    }

    @Override
    public DegreeResponse updateDegreeDuration(
            Long id,
            UpdateDegreeDurationRequest request) {

        Degree degree = findDegree(id);

        degree.setDurationYears(request.getDurationYears());

        return toResponse(degreeRepository.save(degree));
    }

    @Override
    @Transactional(readOnly = true)
    public DegreeResponse getDegreeByCode(String code) {

        String normalizedCode = normalizeCode(code);

        Degree degree = degreeRepository
                .findByDegreeCodeIgnoreCase(normalizedCode)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Degree not found with code: " + normalizedCode
                ));

        return toResponse(degree);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DegreeResponse> getDegreesByLevel(
            DegreeStatus status,
            int page,
            int size) {

        validatePagination(page, size);

        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Degree level is required"
            );
        }

        return degreeRepository.findByLevel(
                status,
                PageRequest.of(
                        page,
                        size,
                        Sort.by("degreeName").ascending()
                )
        ).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DegreeResponse> searchDegrees(
            String name,
            int page,
            int size) {

        validatePagination(page, size);

        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Search name cannot be blank"
            );
        }

        return degreeRepository.findByDegreeNameContainingIgnoreCase(
                name.trim(),
                PageRequest.of(
                        page,
                        size,
                        Sort.by("degreeName").ascending()
                )
        ).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean degreeCodeExists(String code) {

        if (code == null || code.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Degree code is required"
            );
        }

        return degreeRepository.existsByDegreeCodeIgnoreCase(
                normalizeCode(code)
        );
    }


    private Degree findDegree(Long id) {

        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Degree ID must be a positive number"
            );
        }

        return degreeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Degree not found with ID: " + id
                ));
    }

    private String normalizeCode(String code) {

        if (code == null || code.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Degree code is required"
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

    private DegreeResponse toResponse(Degree degree) {

        DegreeResponse response = new DegreeResponse();

        response.setDegreeId(degree.getDegreeId());
        response.setDegreeCode(degree.getDegreeCode());
        response.setDegreeName(degree.getDegreeName());
        response.setLevel(degree.getLevel());
        response.setDurationYears(degree.getDurationYears());


        return response;
    }
}