package com.dstober.onething.repository;

import com.dstober.onething.model.Task;
import com.dstober.onething.model.TimeBracket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findByUserId(Long userId);

  List<Task> findByUserIdAndTimeBracket(Long userId, TimeBracket timeBracket);

  List<Task> findByUserIdAndTimeBracketAndCategoryId(
      Long userId, TimeBracket timeBracket, Long categoryId);

  List<Task> findByUserIdAndTimeBracketAndPriority(
      Long userId, TimeBracket timeBracket, Integer priority);

  List<Task> findByUserIdAndTimeBracketAndCategoryIdAndPriority(
      Long userId, TimeBracket timeBracket, Long categoryId, Integer priority);
}
