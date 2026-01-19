package com.dstober.onething.repository;

import com.dstober.onething.model.Task;
import com.dstober.onething.model.TimeBracket;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

  List<Task> findByUserIdOrderByPriorityDesc(Long userId);

  List<Task> findByUserIdAndTimeBracketOrderByPriorityDesc(Long userId, TimeBracket timeBracket);

  List<Task> findByUserIdAndTimeBracketAndCategoryIdOrderByPriorityDesc(
      Long userId, TimeBracket timeBracket, Long categoryId);

  Optional<Task> findFirstByUserIdAndTimeBracketOrderByPriorityDesc(
      Long userId, TimeBracket timeBracket);

  Optional<Task> findFirstByUserIdAndTimeBracketAndCategoryIdOrderByPriorityDesc(
      Long userId, TimeBracket timeBracket, Long categoryId);
}
