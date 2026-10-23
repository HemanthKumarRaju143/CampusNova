package com.KumarCollege.Controller;

import com.KumarCollege.Enum.DegreeStatus;
import com.KumarCollege.Pojo.Degree.Request.CreateDegreeRequest;
import com.KumarCollege.Pojo.Degree.Response.DegreeResponse;
import com.KumarCollege.Pojo.Degree.Request.UpdateDegreeDurationRequest;
import com.KumarCollege.Service.DegreeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/degrees")
public class DegreeController {

    private final DegreeService degreeService;

    // 1. Create a degree
    @PostMapping
    public ResponseEntity<List<DegreeResponse>> createBulkDegrees(
            @RequestBody List<@Valid CreateDegreeRequest> requests) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(degreeService.createBulkDegrees(requests));
    }

    // 2. Get a degree by ID
    @GetMapping("/{id}")
    public ResponseEntity<DegreeResponse> getDegreeById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                degreeService.getDegreeById(id)
        );
    }

    // 3. Get all degrees with pagination and sorting
    @GetMapping
    public ResponseEntity<Page<DegreeResponse>> getAllDegrees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "degreeName") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        return ResponseEntity.ok(
                degreeService.getAllDegrees(
                        page, size, sortBy, direction
                )
        );
    }

    // 4. Update all editable degree details
    @PutMapping("/{id}")
    public ResponseEntity<DegreeResponse> updateDegree(
            @PathVariable Long id,
            @Valid @RequestBody CreateDegreeRequest request) {

        return ResponseEntity.ok(
                degreeService.updateDegree(id, request)
        );
    }

    // 5. Update only the degree duration
    @PatchMapping("/{id}/duration")
    public ResponseEntity<DegreeResponse> updateDegreeDuration(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDegreeDurationRequest request) {

        return ResponseEntity.ok(
                degreeService.updateDegreeDuration(id, request)
        );
    }

    // 6. Find a degree by its unique code
    @GetMapping("/by-code/{code}")
    public ResponseEntity<DegreeResponse> getDegreeByCode(
            @PathVariable String code) {

        return ResponseEntity.ok(
                degreeService.getDegreeByCode(code)
        );
    }

    // 7. Filter degrees by academic level
    @GetMapping("/by-status/{status}")
    public ResponseEntity<Page<DegreeResponse>> getDegreesByLevel(
            @PathVariable DegreeStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                degreeService.getDegreesByLevel(status, page, size)
        );
    }

    // 8. Search degree names
    @GetMapping("/search")
    public ResponseEntity<Page<DegreeResponse>> searchDegrees(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                degreeService.searchDegrees(name, page, size)
        );
    }

    // 9. Check whether a degree code exists
    @GetMapping("/exists")
    public ResponseEntity<Boolean> degreeCodeExists(
            @RequestParam String code) {

        return ResponseEntity.ok(
                degreeService.degreeCodeExists(code)
        );
    }
}