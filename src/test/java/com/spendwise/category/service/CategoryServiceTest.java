package com.spendwise.category.service;

import com.spendwise.category.dto.CategoryRequest;
import com.spendwise.category.dto.CategoryResponse;
import com.spendwise.category.entity.Category;
import com.spendwise.category.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    @DisplayName("Create category without default budget limit successfully")
    void testCreateCategorySuccess() {
        CategoryRequest req = new CategoryRequest();
        req.setName("Food & Dining");
        req.setDescription("Restaurants and groceries");
        req.setColorCode("#10B981");
        req.setBudgetLimit(null); // No default budget amount!

        Category saved = Category.builder()
                .id(1L)
                .name("Food & Dining")
                .description("Restaurants and groceries")
                .colorCode("#10B981")
                .budgetLimit(BigDecimal.ZERO)
                .userId(100L)
                .build();

        when(categoryRepository.existsAvailableForUserByNormalizedName(100L, "Food & Dining")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryResponse res = categoryService.createCategory(req, 100L);

        assertNotNull(res);
        assertEquals("Food & Dining", res.getName());
        assertEquals(BigDecimal.ZERO, res.getBudgetLimit());
        assertEquals(100L, res.getUserId());
        assertTrue(res.isCustom());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    @DisplayName("Create category with duplicate name throws 409 Conflict")
    void testCreateDuplicateCategoryFails() {
        CategoryRequest req = new CategoryRequest();
        req.setName("Shopping");

        when(categoryRepository.existsAvailableForUserByNormalizedName(100L, "Shopping")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            categoryService.createCategory(req, 100L);
        });

        assertEquals(409, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("already exists"));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Create category rejects duplicate names after trimming and case normalization")
    void testCreateNormalizedDuplicateCategoryFails() {
        CategoryRequest req = new CategoryRequest();
        req.setName("  food   &   dining  ");

        when(categoryRepository.existsAvailableForUserByNormalizedName(100L, "food & dining"))
                .thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                categoryService.createCategory(req, 100L));

        assertEquals(409, ex.getStatusCode().value());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("List categories returns one record for duplicate normalized names")
    void testGetCategoriesDeduplicatesNormalizedNames() {
        Category first = Category.builder().id(1L).name(" Food & Dining ").userId(null).build();
        Category duplicate = Category.builder().id(2L).name("food  &  dining").userId(100L).build();
        when(categoryRepository.findAllAvailableForUser(100L)).thenReturn(List.of(first, duplicate));

        List<CategoryResponse> result = categoryService.getCategories(100L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    @DisplayName("Update category updates fields when owned by user")
    void testUpdateCategorySuccess() {
        Category existing = Category.builder()
                .id(1L)
                .name("Old Name")
                .description("Old desc")
                .colorCode("#10B981")
                .budgetLimit(BigDecimal.valueOf(1000))
                .userId(100L)
                .build();

        CategoryRequest updateReq = new CategoryRequest();
        updateReq.setName("New Name");
        updateReq.setDescription("Updated desc");
        updateReq.setColorCode("#6366F1");
        updateReq.setBudgetLimit(BigDecimal.valueOf(5000));

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsAvailableForUserByNormalizedName(100L, "New Name")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        CategoryResponse res = categoryService.updateCategory(1L, updateReq, 100L);

        assertEquals("New Name", res.getName());
        assertEquals("Updated desc", res.getDescription());
        assertEquals("#6366F1", res.getColorCode());
        assertEquals(BigDecimal.valueOf(5000), res.getBudgetLimit());
    }

    @Test
    @DisplayName("Update category owned by another user throws 403 Forbidden")
    void testUpdateCategoryForbidden() {
        Category existing = Category.builder()
                .id(1L)
                .name("Team Lunch")
                .userId(999L) // Owned by different user
                .build();

        CategoryRequest updateReq = new CategoryRequest();
        updateReq.setName("Hacked Name");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            categoryService.updateCategory(1L, updateReq, 100L); // Current user 100
        });

        assertEquals(403, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("permission"));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Delete category owned by user succeeds")
    void testDeleteCategorySuccess() {
        Category existing = Category.builder()
                .id(5L)
                .name("Hobbies")
                .userId(100L)
                .build();

        when(categoryRepository.findById(5L)).thenReturn(Optional.of(existing));

        categoryService.deleteCategory(5L, 100L);

        verify(categoryRepository).delete(existing);
    }

    @Test
    @DisplayName("Delete category owned by another user or system category throws 403 Forbidden")
    void testDeleteCategoryForbidden() {
        Category systemCat = Category.builder()
                .id(2L)
                .name("General")
                .userId(null) // System category
                .build();

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(systemCat));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            categoryService.deleteCategory(2L, 100L);
        });

        assertEquals(403, ex.getStatusCode().value());
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
