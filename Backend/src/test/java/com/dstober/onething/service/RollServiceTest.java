package com.dstober.onething.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.dstober.onething.TestDataFactory;
import com.dstober.onething.exception.ResourceNotFoundException;
import com.dstober.onething.model.Task;
import com.dstober.onething.repository.TaskRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RollServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private Random random;

    private RollService rollService;

    @BeforeEach
    void setUp() {
        rollService = new RollService(taskRepository, random);
    }

    @Test
    void determineTaskForUser_WithTasks_ReturnsRandomTask() {
        Long userId = 1L;
        Task task1 = TestDataFactory.createTask(1L, "Task 1", userId);
        Task task2 = TestDataFactory.createTask(2L, "Task 2", userId);
        Task task3 = TestDataFactory.createTask(3L, "Task 3", userId);
        List<Task> tasks = List.of(task1, task2, task3);

        when(taskRepository.findByUserId(userId)).thenReturn(tasks);
        when(random.nextInt(3)).thenReturn(1);

        Task result = rollService.determineTaskForUser(userId);

        assertThat(result).isEqualTo(task2);
        assertThat(result.getName()).isEqualTo("Task 2");
    }

    @Test
    void determineTaskForUser_WithSingleTask_ReturnsThatTask() {
        Long userId = 1L;
        Task task = TestDataFactory.createTask(1L, "Only Task", userId);
        List<Task> tasks = List.of(task);

        when(taskRepository.findByUserId(userId)).thenReturn(tasks);
        when(random.nextInt(1)).thenReturn(0);

        Task result = rollService.determineTaskForUser(userId);

        assertThat(result).isEqualTo(task);
        assertThat(result.getName()).isEqualTo("Only Task");
    }

    @Test
    void determineTaskForUser_WithNoTasks_ThrowsResourceNotFoundException() {
        Long userId = 1L;
        when(taskRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> rollService.determineTaskForUser(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No tasks available for user");
    }

    @Test
    void determineTaskForUser_SelectsFirstTask() {
        Long userId = 1L;
        Task task1 = TestDataFactory.createTask(1L, "First Task", userId);
        Task task2 = TestDataFactory.createTask(2L, "Second Task", userId);
        List<Task> tasks = List.of(task1, task2);

        when(taskRepository.findByUserId(userId)).thenReturn(tasks);
        when(random.nextInt(2)).thenReturn(0);

        Task result = rollService.determineTaskForUser(userId);

        assertThat(result).isEqualTo(task1);
    }

    @Test
    void determineTaskForUser_SelectsLastTask() {
        Long userId = 1L;
        Task task1 = TestDataFactory.createTask(1L, "First Task", userId);
        Task task2 = TestDataFactory.createTask(2L, "Last Task", userId);
        List<Task> tasks = List.of(task1, task2);

        when(taskRepository.findByUserId(userId)).thenReturn(tasks);
        when(random.nextInt(2)).thenReturn(1);

        Task result = rollService.determineTaskForUser(userId);

        assertThat(result).isEqualTo(task2);
        assertThat(result.getName()).isEqualTo("Last Task");
    }
}
