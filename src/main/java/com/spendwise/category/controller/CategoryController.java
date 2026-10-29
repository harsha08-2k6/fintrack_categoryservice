package com.spendwise.category.controller;

import com.spendwise.category.dto.CategoryRequest;
import com.spendwise.category.dto.CategoryResponse;
import com.spendwise.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    private void validateUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User authentication required");
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) Long userId
    ) {
        validateUser(userId);
        return ResponseEntity.ok(categoryService.getCategories(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id,
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) Long userId
    ) {
        return ResponseEntity.ok(categoryService.getCategoryById(id, userId));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest request,
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) Long userId
    ) {
        validateUser(userId);
        CategoryResponse response = categoryService.createCategory(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request,
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) Long userId
    ) {
        validateUser(userId);
        CategoryResponse response = categoryService.updateCategory(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            @RequestHeader(value = "X-Authenticated-User-Id", required = false) Long userId
    ) {
        validateUser(userId);
        categoryService.deleteCategory(id, userId);
        return ResponseEntity.noContent().build();
    }
}
