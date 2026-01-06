package com.dstober.onething.repository;

import com.dstober.onething.model.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  List<Category> findByUserId(Long userId);
}
