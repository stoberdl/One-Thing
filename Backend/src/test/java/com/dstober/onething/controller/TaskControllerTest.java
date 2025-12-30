package com.dstober.onething.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dstober.onething.exception.ResourceNotFoundException;
import com.dstober.onething.model.Task;
import com.dstober.onething.model.TimeBracket;
import com.dstober.onething.service.RollService;
import com.dstober.onething.service.TaskService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private TaskService taskService;
  @MockBean private RollService rollService;

  @Test
  void shouldCreateTask() throws Exception {
    Task savedTask = new Task();
    savedTask.setId(1L);
    savedTask.setName("Clean floors");
    savedTask.setCategoryId(1L);
    savedTask.setTimeBracket(TimeBracket.FIFTEEN_TO_THIRTY);
    savedTask.setPriority(2);
    savedTask.setUserId(1L);

    when(taskService.createTask(any(Task.class))).thenReturn(savedTask);

    String requestBody =
        """
        {
            "name": "Clean floors",
            "categoryId": 1,
            "timeBracket": "15-30",
            "priority": 2,
            "userId": 1
        }
        """;

    mockMvc
        .perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Clean floors"))
        .andExpect(jsonPath("$.timeBracket").value("15-30"));
  }

  @Test
  void shouldGetTaskById() throws Exception {
    Task task = createMockTask();

    when(taskService.getTaskById(1L)).thenReturn(task);

    mockMvc
        .perform(get("/api/tasks/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Clean floors"))
        .andExpect(jsonPath("$.timeBracket").value("15-30"));
  }

  @Test
  void shouldGetTasksByUserId() throws Exception {
    List<Task> tasks = Arrays.asList(createMockTask(), createMockTask());

    when(taskService.getAllTasksByUserId(1L)).thenReturn(tasks);

    mockMvc
        .perform(get("/api/tasks").param("userId", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  void shouldUpdateTask() throws Exception {
    Task updatedTask = createMockTask();
    updatedTask.setName("Updated task");

    when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(updatedTask);

    String requestBody =
        """
        {
            "name": "Updated task",
            "categoryId": 1,
            "timeBracket": "15-30",
            "priority": 2,
            "userId": 1
        }
        """;

    mockMvc
        .perform(put("/api/tasks/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated task"));
  }

  @Test
  void shouldDeleteTask() throws Exception {
    doNothing().when(taskService).deleteTask(1L);

    mockMvc.perform(delete("/api/tasks/1")).andExpect(status().isNoContent());
  }

  @Test
  void shouldReturn404WhenTaskNotFound() throws Exception {
    when(taskService.getTaskById(999L))
        .thenThrow(new ResourceNotFoundException("Task not found with id: 999"));

    mockMvc
        .perform(get("/api/tasks/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Task not found with id: 999"));
  }

  private Task createMockTask() {
    Task task = new Task();
    task.setId(1L);
    task.setName("Clean floors");
    task.setCategoryId(1L);
    task.setTimeBracket(TimeBracket.FIFTEEN_TO_THIRTY);
    task.setPriority(2);
    task.setUserId(1L);
    return task;
  }
}
