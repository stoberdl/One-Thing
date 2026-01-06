package com.dstober.onething.service;

import com.dstober.onething.dto.CategoryCreateRequest;
import com.dstober.onething.model.Category;
import com.dstober.onething.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public Category createCategory(CategoryCreateRequest request, Long authenticatedUserId) {
    Category category =
        new Category(request.getName(), request.getIcon(), request.getColor(), authenticatedUserId);
    return categoryRepository.save(category);
  }
}
