package com.dstober.onething.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dstober.onething.TestDataFactory;
import com.dstober.onething.dto.CategoryCreateRequest;
import com.dstober.onething.model.Category;
import com.dstober.onething.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void createCategory_Success() {
        Long userId = 1L;
        CategoryCreateRequest request = TestDataFactory.createCategoryCreateRequest();
        Category savedCategory = TestDataFactory.createCategory(userId);

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Category result = categoryService.createCategory(request, userId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(TestDataFactory.DEFAULT_CATEGORY_NAME);
        assertThat(result.getIcon()).isEqualTo(TestDataFactory.DEFAULT_ICON);
        assertThat(result.getColor()).isEqualTo(TestDataFactory.DEFAULT_COLOR);
        assertThat(result.getUserId()).isEqualTo(userId);
    }

    @Test
    void createCategory_SetsCorrectUserId() {
        Long userId = 5L;
        CategoryCreateRequest request = TestDataFactory.createCategoryCreateRequest("Work", "briefcase", "#3498DB");
        Category savedCategory = TestDataFactory.createCategory("Work", "briefcase", "#3498DB", userId);

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Category result = categoryService.createCategory(request, userId);

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(categoryCaptor.capture());

        Category capturedCategory = categoryCaptor.getValue();
        assertThat(capturedCategory.getUserId()).isEqualTo(userId);
        assertThat(capturedCategory.getName()).isEqualTo("Work");
        assertThat(capturedCategory.getIcon()).isEqualTo("briefcase");
        assertThat(capturedCategory.getColor()).isEqualTo("#3498DB");
    }

    @Test
    void createCategory_WithDifferentColors() {
        Long userId = 1L;
        CategoryCreateRequest request = TestDataFactory.createCategoryCreateRequest("Personal", "home", "#FF0000");
        Category savedCategory = TestDataFactory.createCategory("Personal", "home", "#FF0000", userId);

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Category result = categoryService.createCategory(request, userId);

        assertThat(result.getColor()).isEqualTo("#FF0000");
    }
}
