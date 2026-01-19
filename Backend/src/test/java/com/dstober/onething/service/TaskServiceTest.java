package com.dstober.onething.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.dstober.onething.TestDataFactory;
import com.dstober.onething.dto.TaskCreateRequest;
import com.dstober.onething.exception.ResourceNotFoundException;
import com.dstober.onething.model.Frequency;
import com.dstober.onething.model.Task;
import com.dstober.onething.model.TimeBracket;
import com.dstober.onething.repository.TaskRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

  @Mock private TaskRepository taskRepository;

  private TaskService taskService;

  @BeforeEach
  void setUp() {
    taskService = new TaskService(taskRepository);
  }

  @Test
  void createTask_Success() {
    Long userId = 1L;
    TaskCreateRequest request = TestDataFactory.createTaskCreateRequest();
    Task savedTask = TestDataFactory.createTask();

    when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

    Task result = taskService.createTask(request, userId);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(TestDataFactory.DEFAULT_TASK_NAME);
    assertThat(result.getUserId()).isEqualTo(userId);

    ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
    verify(taskRepository).save(taskCaptor.capture());
    Task capturedTask = taskCaptor.getValue();
    assertThat(capturedTask.getName()).isEqualTo(request.getName());
    assertThat(capturedTask.getCategoryId()).isEqualTo(request.getCategoryId());
    assertThat(capturedTask.getTimeBracket()).isEqualTo(request.getTimeBracket());
    assertThat(capturedTask.getFrequency()).isEqualTo(request.getFrequency());
    assertThat(capturedTask.getUserId()).isEqualTo(userId);
  }

  @Test
  void createTask_WithParentId() {
    Long userId = 1L;
    Long parentId = 10L;
    TaskCreateRequest request = TestDataFactory.createTaskCreateRequestWithParent(parentId);
    Task savedTask = TestDataFactory.createTaskWithParent(parentId);

    when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

    Task result = taskService.createTask(request, userId);

    assertThat(result.getParentId()).isEqualTo(parentId);

    ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
    verify(taskRepository).save(taskCaptor.capture());
    assertThat(taskCaptor.getValue().getParentId()).isEqualTo(parentId);
  }

  @Test
  void createTask_SetsAllFieldsCorrectly() {
    Long userId = 5L;
    TaskCreateRequest request =
        new TaskCreateRequest("Custom Task", 2L, TimeBracket.OVER_THIRTY, Frequency.WEEKLY, null);
    Task savedTask = new Task();
    savedTask.setId(1L);
    savedTask.setName("Custom Task");
    savedTask.setCategoryId(2L);
    savedTask.setTimeBracket(TimeBracket.OVER_THIRTY);
    savedTask.setFrequency(Frequency.WEEKLY);
    savedTask.setUserId(userId);

    when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

    Task result = taskService.createTask(request, userId);

    assertThat(result.getName()).isEqualTo("Custom Task");
    assertThat(result.getCategoryId()).isEqualTo(2L);
    assertThat(result.getTimeBracket()).isEqualTo(TimeBracket.OVER_THIRTY);
    assertThat(result.getFrequency()).isEqualTo(Frequency.WEEKLY);
  }

  @Test
  void getTaskByIdAndUserId_Found() {
    Long taskId = 1L;
    Long userId = 1L;
    Task task = TestDataFactory.createTask(taskId, "My Task", userId);

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

    Task result = taskService.getTaskByIdAndUserId(taskId, userId);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(taskId);
    assertThat(result.getUserId()).isEqualTo(userId);
  }

  @Test
  void getTaskByIdAndUserId_NotFound() {
    Long taskId = 999L;
    Long userId = 1L;

    when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.getTaskByIdAndUserId(taskId, userId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Task not found with id: " + taskId);
  }

  @Test
  void getTaskByIdAndUserId_WrongUser() {
    Long taskId = 1L;
    Long taskOwnerId = 1L;
    Long requestingUserId = 2L;
    Task task = TestDataFactory.createTask(taskId, "Task", taskOwnerId);

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

    assertThatThrownBy(() -> taskService.getTaskByIdAndUserId(taskId, requestingUserId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Task not found with id: " + taskId);
  }

  @Test
  void getAllTasksByUserId_WithTasks() {
    Long userId = 1L;
    Task task1 = TestDataFactory.createTask(1L, "Task 1", userId);
    Task task2 = TestDataFactory.createTask(2L, "Task 2", userId);
    List<Task> tasks = List.of(task1, task2);

    when(taskRepository.findByUserIdOrderByPriorityDesc(userId)).thenReturn(tasks);

    List<Task> result = taskService.getAllTasksByUserId(userId);

    assertThat(result).hasSize(2);
    assertThat(result).containsExactlyInAnyOrder(task1, task2);
  }

  @Test
  void getAllTasksByUserId_Empty() {
    Long userId = 1L;

    when(taskRepository.findByUserIdOrderByPriorityDesc(userId))
        .thenReturn(Collections.emptyList());

    List<Task> result = taskService.getAllTasksByUserId(userId);

    assertThat(result).isEmpty();
  }

  @Test
  void updateTask_Success() {
    Long taskId = 1L;
    Long userId = 1L;
    Task existingTask = TestDataFactory.createTask(taskId, "Original", userId);
    Task taskDetails = new Task();
    taskDetails.setName("Updated Name");
    taskDetails.setCategoryId(2L);
    taskDetails.setTimeBracket(TimeBracket.OVER_THIRTY);
    taskDetails.setFrequency(Frequency.QUARTERLY);
    taskDetails.setParentId(5L);

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
    when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Task result = taskService.updateTask(taskId, taskDetails, userId);

    assertThat(result.getName()).isEqualTo("Updated Name");
    assertThat(result.getCategoryId()).isEqualTo(2L);
    assertThat(result.getTimeBracket()).isEqualTo(TimeBracket.OVER_THIRTY);
    assertThat(result.getFrequency()).isEqualTo(Frequency.QUARTERLY);
    assertThat(result.getParentId()).isEqualTo(5L);
  }

  @Test
  void updateTask_NotFound() {
    Long taskId = 999L;
    Long userId = 1L;
    Task taskDetails = new Task();

    when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.updateTask(taskId, taskDetails, userId))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void updateTask_WrongUser() {
    Long taskId = 1L;
    Long ownerId = 1L;
    Long requesterId = 2L;
    Task existingTask = TestDataFactory.createTask(taskId, "Task", ownerId);
    Task taskDetails = new Task();

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

    assertThatThrownBy(() -> taskService.updateTask(taskId, taskDetails, requesterId))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void deleteTask_Success() {
    Long taskId = 1L;
    Long userId = 1L;
    Task task = TestDataFactory.createTask(taskId, "To Delete", userId);

    when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
    doNothing().when(taskRepository).delete(task);

    taskService.deleteTask(taskId, userId);

    verify(taskRepository).delete(task);
  }

  @Test
  void deleteTask_NotFound() {
    Long taskId = 999L;
    Long userId = 1L;

    when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.deleteTask(taskId, userId))
        .isInstanceOf(ResourceNotFoundException.class);
  }
}
