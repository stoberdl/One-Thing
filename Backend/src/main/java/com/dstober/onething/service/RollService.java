package com.dstober.onething.service;

import com.dstober.onething.dto.RollRequest;
import com.dstober.onething.exception.ResourceNotFoundException;
import com.dstober.onething.model.Task;
import com.dstober.onething.repository.TaskRepository;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class RollService {

  private final TaskRepository taskRepository;
  private final Random random;

  public RollService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
    this.random = new Random();
  }

  // Constructor for testing with injected Random
  //  RollService(TaskRepository taskRepository, Random random) {doesnt build normal app with this
  //    this.taskRepository = taskRepository;
  //    this.random = random;
  //  }

  public Task determineTaskForUser(Long userId, RollRequest rollRequest) {
    List<Task> tasks;
    if (rollRequest.rollTemperature().priorityToRandomness() <= 0.05
        && rollRequest.categoryId() == null) {
      return taskRepository
          .findFirstByUserIdAndTimeBracketOrderByPriorityAsc(userId, rollRequest.timeBracket())
          .orElseThrow();
    } else if (rollRequest.rollTemperature().priorityToRandomness() <= 0.05) {
      return taskRepository
          .findFirstByUserIdAndTimeBracketAndCategoryIdOrderByPriorityAsc(
              userId, rollRequest.timeBracket(), rollRequest.categoryId())
          .orElseThrow();
    } else if (rollRequest.categoryId() != null) {
      tasks =
          taskRepository.findByUserIdAndTimeBracketAndCategoryId(
              userId, rollRequest.timeBracket(), rollRequest.categoryId());
    } else {
      tasks = taskRepository.findByUserIdAndTimeBracket(userId, rollRequest.timeBracket());
    }

    if (tasks.isEmpty()) {
      throw new ResourceNotFoundException("No tasks available for user");
    }

    int randomIndex = random.nextInt(tasks.size());
    return tasks.get(randomIndex);
  }
}
