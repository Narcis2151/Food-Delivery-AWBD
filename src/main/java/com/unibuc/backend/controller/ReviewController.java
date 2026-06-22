package com.unibuc.backend.controller;

import com.unibuc.backend.dto.request.ReviewRequest;
import com.unibuc.backend.dto.response.ReviewResponse;
import com.unibuc.backend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@AllArgsConstructor
@Tag(name = "Reviews", description = "Endpoints for managing reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Get All Reviews", description = "Retrieve all reviews")
    public ResponseEntity<List<ReviewResponse>> findAll() {
        return ResponseEntity.ok(reviewService.findAll());
    }

    @GetMapping("/menu-item/{menuItemId}")
    @Operation(summary = "Get Reviews By Menu Item", description = "Retrieve all reviews for a given menu item")
    public ResponseEntity<List<ReviewResponse>> findByMenuItemId(@PathVariable Long menuItemId) {
        return ResponseEntity.ok(reviewService.findByMenuItemId(menuItemId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Review By ID", description = "Retrieve a single review by its ID")
    public ResponseEntity<ReviewResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create Review", description = "Create a new review. The authenticated user becomes the author.")
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Review", description = "Update a review. Only the review author can perform this action.")
    public ResponseEntity<ReviewResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Review", description = "Delete a review. Only the review author can perform this action.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
