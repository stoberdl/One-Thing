package com.dstober.onething.service;

import com.dstober.onething.model.Task;
import com.dstober.onething.repository.TaskRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PriorityCalculationService {

  private final TaskRepository taskRepository;

  public PriorityCalculationService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public void recalculatePrioritiesForUser(Long userId) {
    List<Task> tasks = taskRepository.findByUserId(userId);
    Instant now = Instant.now();

    for (Task task : tasks) {
      int priority = calculatePriority(task, now);
      task.setPriority(priority);
    }

    taskRepository.saveAll(tasks);
  }

  // Scale to 0-100 range, cap at 100
  // 0 = just completed
  // 20 = 1x frequency elapsed (due now)
  // 40 = 2x overdue
  // 100 = 5x+ overdue (max)
  private int calculatePriority(Task task, Instant now) {
    Instant lastActivity =
        task.getLastCompleted() != null ? task.getLastCompleted() : task.getCreatedAt();

    long daysSince = ChronoUnit.DAYS.between(lastActivity, now);
    double overdueFactor = (double) daysSince / task.getFrequency().getDays();

    return (int) Math.min(Math.round(overdueFactor * 20), 100);
  }
}
