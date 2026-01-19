package com.dstober.onething.service;

import com.dstober.onething.dto.RollPreference;
import com.dstober.onething.dto.RollRequest;
import com.dstober.onething.exception.ResourceNotFoundException;
import com.dstober.onething.model.Task;
import com.dstober.onething.repository.TaskRepository;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class RollService {

  public static final double PRIORITY_THRESHOLD = 0.05;
  private final TaskRepository taskRepository;
  private final Random random;

  public RollService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
    this.random = new Random();
  }

  public Task determineTaskForUser(Long userId, RollRequest rollRequest) {
    List<Task> tasks;
    if (rollRequest.rollTemperature().priorityToRandomness() <= PRIORITY_THRESHOLD
        && rollRequest.categoryId() == null) {
      return taskRepository
          .findFirstByUserIdAndTimeBracketOrderByPriorityDesc(userId, rollRequest.timeBracket())
          .orElseThrow();
    } else if (rollRequest.rollTemperature().priorityToRandomness() <= PRIORITY_THRESHOLD) {
      return taskRepository
          .findFirstByUserIdAndTimeBracketAndCategoryIdOrderByPriorityDesc(
              userId, rollRequest.timeBracket(), rollRequest.categoryId())
          .orElseThrow();
    } else if (rollRequest.categoryId() != null) {
      tasks =
          taskRepository.findByUserIdAndTimeBracketAndCategoryIdOrderByPriorityDesc(
              userId, rollRequest.timeBracket(), rollRequest.categoryId());
    } else {
      tasks =
          taskRepository.findByUserIdAndTimeBracketOrderByPriorityDesc(
              userId, rollRequest.timeBracket());
    }

    if (tasks.isEmpty()) {
      throw new ResourceNotFoundException("No tasks available for user");
    }
    return rollTask(tasks, rollRequest.rollTemperature());
  }

  private Task rollTask(List<Task> tasks, @NotNull RollPreference rollPreference) {

    int n = tasks.size();
    double[] weights = new double[n];
    double totalWeight = 0;

    double exponent = 1 + Math.pow(1 - rollPreference.priorityToRandomness(), 2) * 4;
    double blendFactor = Math.sqrt(rollPreference.priorityToRandomness());

    for (int i = 0; i < n; i++) {
      double rankWeight = n - i;
      double priorityWeight = Math.pow(rankWeight, exponent);
      weights[i] = priorityWeight * (1 - blendFactor) + blendFactor;
      totalWeight += weights[i];
    }

    double roll = random.nextDouble() * totalWeight;
    double cumulative = 0;

    for (int i = 0; i < n; i++) {
      cumulative += weights[i];
      if (roll < cumulative) {
        return tasks.get(i);
      }
    }
    return tasks.get(n - 1);
  }
}
