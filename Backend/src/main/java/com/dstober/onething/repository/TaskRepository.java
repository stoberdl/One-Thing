package com.dstober.onething.repository;

import com.dstober.onething.model.Task;
import com.dstober.onething.model.TimeBracket;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findByUserId(Long userId);

  List<Task> findByUserIdAndTimeBracket(Long userId, TimeBracket timeBracket);

  List<Task> findByUserIdAndTimeBracketAndCategoryId(
      Long userId, TimeBracket timeBracket, Long categoryId);

  Optional<Task> findFirstByUserIdAndTimeBracketOrderByPriorityAsc(
      Long userId, TimeBracket timeBracket);

  Optional<Task> findFirstByUserIdAndTimeBracketAndCategoryIdOrderByPriorityAsc(
      Long userId, TimeBracket timeBracket, Long categoryId);
}
