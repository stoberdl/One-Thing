package com.dstober.onething.repository;

import com.dstober.onething.model.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findByUserId(Long userId);

  Integer countByUserId(Long userId); // change
}
