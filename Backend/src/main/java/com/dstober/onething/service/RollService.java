package com.dstober.onething.service;

import com.dstober.onething.exception.ResourceNotFoundException;
import com.dstober.onething.model.Task;
import com.dstober.onething.repository.TaskRepository;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class RollService {

  private final TaskRepository taskRepository;

  public RollService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public Task determineTaskForUser(Long userId) {
    Random rand = new Random();
    Long randomTaskId = rand.nextLong(taskRepository.countByUserId(userId)) + 1;
    // todo:change to list of task ids that user owns, randomly choose one in list
    // todo: change to Int's?? not gonna need longs
    return taskRepository
        .findById(randomTaskId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Random task not found with id: " + randomTaskId));
  }
}
