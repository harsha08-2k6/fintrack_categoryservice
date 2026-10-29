package com.spendwise.category.service;

import com.spendwise.category.dto.CategoryRequest;
import com.spendwise.category.dto.CategoryResponse;
import com.spendwise.category.entity.Category;
import com.spendwise.category.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getCategories(Long userId) {
        List<Category> categories;
        if (userId != null) {
            categories = categoryRepository.findAllAvailableForUser(userId);
        } else {
            categories = categoryRepository.findAll();
        }
        Map<String, Category> uniqueCategories = new LinkedHashMap<>();
        categories.forEach(category -> uniqueCategories.putIfAbsent(normalizeName(category.getName()), category));
        return uniqueCategories.values().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id, Long userId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + id));
        if (category.getUserId() != null && !category.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view this category");
        }
        return mapToResponse(category);
    }

    public CategoryResponse createCategory(CategoryRequest request, Long userId) {
        String trimmedName = normalizeDisplayName(request.getName());
        if (categoryRepository.existsAvailableForUserByNormalizedName(userId, trimmedName)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A category with the name '" + trimmedName + "' already exists");
        }

        Category category = Category.builder()
                .name(trimmedName)
                .description(request.getDescription())
                .icon(request.getIcon() != null && !request.getIcon().isBlank() ? request.getIcon() : "tag")
                .colorCode(request.getColorCode() != null && !request.getColorCode().isBlank() ? request.getColorCode() : "#6366f1")
                .budgetLimit(request.getBudgetLimit() != null ? request.getBudgetLimit() : BigDecimal.ZERO)
                .userId(userId)
                .build();

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request, Long userId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        if (userId == null || category.getUserId() == null || !category.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to edit this category");
        }

        String trimmedName = normalizeDisplayName(request.getName());
        if (!normalizeName(category.getName()).equals(normalizeName(trimmedName)) &&
                categoryRepository.existsAvailableForUserByNormalizedName(userId, trimmedName)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A category with the name '" + trimmedName + "' already exists");
        }

        category.setName(trimmedName);
        if (request.getDescription() != null) category.setDescription(request.getDescription());
        if (request.getIcon() != null) category.setIcon(request.getIcon());
        if (request.getColorCode() != null) category.setColorCode(request.getColorCode());
        if (request.getBudgetLimit() != null) category.setBudgetLimit(request.getBudgetLimit());

        Category updated = categoryRepository.save(category);
        return mapToResponse(updated);
    }

    public void deleteCategory(Long id, Long userId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        if (userId == null || category.getUserId() == null || !category.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this category");
        }

        categoryRepository.delete(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .icon(category.getIcon())
                .colorCode(category.getColorCode())
                .budgetLimit(category.getBudgetLimit())
                .userId(category.getUserId())
                .isCustom(category.getUserId() != null)
                .createdAt(category.getCreatedAt())
                .build();
    }

    private String normalizeName(String name) {
        return normalizeDisplayName(name).toLowerCase(Locale.ROOT);
    }

    private String normalizeDisplayName(String name) {
        return name == null ? "" : name.trim().replaceAll("\\s+", " ");
    }
}
