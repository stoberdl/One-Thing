package com.dstober.onething.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.dstober.onething.TestDataFactory;
import com.dstober.onething.model.Frequency;
import com.dstober.onething.model.Task;
import com.dstober.onething.repository.TaskRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PriorityCalculationServiceTest {

  @Mock private TaskRepository taskRepository;

  private PriorityCalculationService priorityCalculationService;

  @BeforeEach
  void setUp() {
    priorityCalculationService = new PriorityCalculationService(taskRepository);
  }

  @Test
  void recalculatePrioritiesForUser_JustCompleted_PriorityZero() {
    Long userId = 1L;
    Task task = TestDataFactory.createTask();
    task.setFrequency(Frequency.WEEKLY);
    task.setLastCompleted(Instant.now()); // Just completed

    when(taskRepository.findByUserIdOrderByPriorityDesc(userId)).thenReturn(List.of(task));

    priorityCalculationService.recalculatePrioritiesForUser(userId);

    assertThat(task.getPriority()).isEqualTo(0);
    verify(taskRepository).saveAll(List.of(task));
  }

  @Test
  void recalculatePrioritiesForUser_WeeklyTask_OneDueDate_Priority20() {
    Long userId = 1L;
    Task task = TestDataFactory.createTask();
    task.setFrequency(Frequency.WEEKLY);
    task.setLastCompleted(Instant.now().minus(7, ChronoUnit.DAYS)); // 1 week ago

    when(taskRepository.findByUserIdOrderByPriorityDesc(userId)).thenReturn(List.of(task));

    priorityCalculationService.recalculatePrioritiesForUser(userId);

    // 7 days / 7 days = 1.0, * 20 = 20
    assertThat(task.getPriority()).isEqualTo(20);
  }

  @Test
  void recalculatePrioritiesForUser_WeeklyTask_TwoWeeksOverdue_Priority40() {
    Long userId = 1L;
    Task task = TestDataFactory.createTask();
    task.setFrequency(Frequency.WEEKLY);
    task.setLastCompleted(Instant.now().minus(14, ChronoUnit.DAYS)); // 2 weeks ago

    when(taskRepository.findByUserIdOrderByPriorityDesc(userId)).thenReturn(List.of(task));

    priorityCalculationService.recalculatePrioritiesForUser(userId);

    // 14 days / 7 days = 2.0, * 20 = 40
    assertThat(task.getPriority()).isEqualTo(40);
  }

  @Test
  void recalculatePrioritiesForUser_MonthlyTask_OneDueDate_Priority20() {
    Long userId = 1L;
    Task task = TestDataFactory.createTask();
    task.setFrequency(Frequency.MONTHLY);
    task.setLastCompleted(Instant.now().minus(30, ChronoUnit.DAYS)); // 1 month ago

    when(taskRepository.findByUserIdOrderByPriorityDesc(userId)).thenReturn(List.of(task));

    priorityCalculationService.recalculatePrioritiesForUser(userId);

    // 30 days / 30 days = 1.0, * 20 = 20
    assertThat(task.getPriority()).isEqualTo(20);
  }

  @Test
  void recalculatePrioritiesForUser_MaxPriorityCap_At100() {
    Long userId = 1L;
    Task task = TestDataFactory.createTask();
    task.setFrequency(Frequency.WEEKLY);
    task.setLastCompleted(Instant.now().minus(100, ChronoUnit.DAYS)); // Way overdue

    when(taskRepository.findByUserIdOrderByPriorityDesc(userId)).thenReturn(List.of(task));

    priorityCalculationService.recalculatePrioritiesForUser(userId);

    // Should cap at 100
    assertThat(task.getPriority()).isEqualTo(100);
  }

  @Test
  void recalculatePrioritiesForUser_NeverCompleted_UsesCreatedAt() {
    Long userId = 1L;
    Task task = TestDataFactory.createTask();
    task.setFrequency(Frequency.WEEKLY);
    task.setLastCompleted(null);
    task.setCreatedAt(Instant.now().minus(7, ChronoUnit.DAYS)); // Created 1 week ago

    when(taskRepository.findByUserIdOrderByPriorityDesc(userId)).thenReturn(List.of(task));

    priorityCalculationService.recalculatePrioritiesForUser(userId);

    // Uses createdAt: 7 days / 7 days = 1.0, * 20 = 20
    assertThat(task.getPriority()).isEqualTo(20);
  }

  @Test
  void recalculatePrioritiesForUser_MultipleTasks_AllUpdated() {
    Long userId = 1L;
    Task task1 = TestDataFactory.createTask(1L, "Task 1", userId);
    task1.setFrequency(Frequency.WEEKLY);
    task1.setLastCompleted(Instant.now().minus(7, ChronoUnit.DAYS));

    Task task2 = TestDataFactory.createTask(2L, "Task 2", userId);
    task2.setFrequency(Frequency.MONTHLY);
    task2.setLastCompleted(Instant.now().minus(60, ChronoUnit.DAYS));

    when(taskRepository.findByUserIdOrderByPriorityDesc(userId)).thenReturn(List.of(task1, task2));

    priorityCalculationService.recalculatePrioritiesForUser(userId);

    assertThat(task1.getPriority()).isEqualTo(20); // 7/7 * 20 = 20
    assertThat(task2.getPriority()).isEqualTo(40); // 60/30 * 20 = 40

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Task>> captor = ArgumentCaptor.forClass(List.class);
    verify(taskRepository).saveAll(captor.capture());
    assertThat(captor.getValue()).hasSize(2);
  }

  @Test
  void recalculatePrioritiesForUser_EmptyTaskList_NoSaveNeeded() {
    Long userId = 1L;

    when(taskRepository.findByUserIdOrderByPriorityDesc(userId)).thenReturn(List.of());

    priorityCalculationService.recalculatePrioritiesForUser(userId);

    verify(taskRepository).saveAll(List.of());
  }

  @Test
  void recalculatePrioritiesForUser_QuarterlyTask_HalfwayThrough_Priority10() {
    Long userId = 1L;
    Task task = TestDataFactory.createTask();
    task.setFrequency(Frequency.QUARTERLY); // 90 days
    task.setLastCompleted(Instant.now().minus(45, ChronoUnit.DAYS)); // Halfway

    when(taskRepository.findByUserIdOrderByPriorityDesc(userId)).thenReturn(List.of(task));

    priorityCalculationService.recalculatePrioritiesForUser(userId);

    // 45 days / 90 days = 0.5, * 20 = 10
    assertThat(task.getPriority()).isEqualTo(10);
  }
}
