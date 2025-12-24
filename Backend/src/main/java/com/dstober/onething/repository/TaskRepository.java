package com.dstober.onething.repository;

import com.dstober.onething.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    Integer countByUserId(Long userId);//change
}
