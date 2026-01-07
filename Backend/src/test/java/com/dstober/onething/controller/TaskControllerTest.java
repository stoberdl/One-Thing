package com.dstober.onething.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dstober.onething.dto.CategoryCreateRequest;
import com.dstober.onething.dto.TaskCreateRequest;
import com.dstober.onething.exception.GlobalExceptionHandler;
import com.dstober.onething.model.Category;
import com.dstober.onething.model.Task;
import com.dstober.onething.model.TimeBracket;
import com.dstober.onething.security.JwtAuthenticationFilter;
import com.dstober.onething.security.UserPrincipal;
import com.dstober.onething.service.CategoryService;
import com.dstober.onething.service.RollService;
import com.dstober.onething.service.TaskService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = TaskController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = JwtAuthenticationFilter.class))
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
public class TaskControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private TaskService taskService;
  @MockBean private RollService rollService;
  @MockBean private CategoryService categoryService;

  private org.springframework.security.core.Authentication createAuthentication() {
    UserPrincipal userPrincipal =
        new UserPrincipal(1L, "test@example.com", "password", "Test User");
    return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
        userPrincipal, null, userPrincipal.getAuthorities());
  }

  @Test
  void shouldCreateTask() throws Exception {
    Task savedTask = new Task();
    savedTask.setId(1L);
    savedTask.setName("Clean floors");
    savedTask.setCategoryId(1L);
    savedTask.setTimeBracket(TimeBracket.FIFTEEN_TO_THIRTY);
    savedTask.setPriority(2);
    savedTask.setUserId(1L);

    when(taskService.createTask(any(TaskCreateRequest.class), eq(1L))).thenReturn(savedTask);

    String requestBody =
        """
        {
            "name": "Clean floors",
            "categoryId": 1,
            "timeBracket": "15-30",
            "priority": 2
        }
        """;

    mockMvc
        .perform(
            post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(authentication(createAuthentication())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Clean floors"))
        .andExpect(jsonPath("$.categoryId").value(1))
        .andExpect(jsonPath("$.priority").value(2));
  }

  @Test
  void shouldCreateTaskWithParentId() throws Exception {
    Task savedTask = new Task();
    savedTask.setId(2L);
    savedTask.setName("Subtask");
    savedTask.setCategoryId(1L);
    savedTask.setTimeBracket(TimeBracket.UNDER_FIFTEEN);
    savedTask.setPriority(1);
    savedTask.setUserId(1L);
    savedTask.setParentId(1L);

    when(taskService.createTask(any(TaskCreateRequest.class), eq(1L))).thenReturn(savedTask);

    String requestBody =
        """
        {
            "name": "Subtask",
            "categoryId": 1,
            "timeBracket": "<15",
            "priority": 1,
            "parentId": 1
        }
        """;

    mockMvc
        .perform(
            post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(authentication(createAuthentication())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(2))
        .andExpect(jsonPath("$.parentId").value(1));
  }

  @Test
  void shouldCreateCategory() throws Exception {
    Category savedCategory = new Category("Work", "briefcase", "#FF5733", 1L);

    when(categoryService.createCategory(any(CategoryCreateRequest.class), eq(1L)))
        .thenReturn(savedCategory);

    String requestBody =
        """
        {
            "name": "Work",
            "icon": "briefcase",
            "color": "#FF5733"
        }
        """;

    mockMvc
        .perform(
            post("/api/tasks/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(authentication(createAuthentication())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Work"))
        .andExpect(jsonPath("$.icon").value("briefcase"))
        .andExpect(jsonPath("$.color").value("#FF5733"));
  }

  @Test
  void shouldCreateCategoryWithDifferentColor() throws Exception {
    Category savedCategory = new Category("Personal", "home", "#3498DB", 1L);

    when(categoryService.createCategory(any(CategoryCreateRequest.class), eq(1L)))
        .thenReturn(savedCategory);

    String requestBody =
        """
        {
            "name": "Personal",
            "icon": "home",
            "color": "#3498DB"
        }
        """;

    mockMvc
        .perform(
            post("/api/tasks/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .with(authentication(createAuthentication())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Personal"))
        .andExpect(jsonPath("$.icon").value("home"))
        .andExpect(jsonPath("$.color").value("#3498DB"));
  }

  @Test
  void shouldGetAllTasksForUser() throws Exception {
    Task task1 = new Task();
    task1.setId(1L);
    task1.setName("Task 1");
    task1.setCategoryId(1L);
    task1.setTimeBracket(TimeBracket.FIFTEEN_TO_THIRTY);
    task1.setPriority(1);
    task1.setUserId(1L);

    Task task2 = new Task();
    task2.setId(2L);
    task2.setName("Task 2");
    task2.setCategoryId(2L);
    task2.setTimeBracket(TimeBracket.UNDER_FIFTEEN);
    task2.setPriority(2);
    task2.setUserId(1L);

    when(taskService.getAllTasksByUserId(1L)).thenReturn(List.of(task1, task2));

    mockMvc
        .perform(get("/api/tasks").with(authentication(createAuthentication())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("Task 1"))
        .andExpect(jsonPath("$[1].id").value(2))
        .andExpect(jsonPath("$[1].name").value("Task 2"));
  }

  @Test
  void shouldReturnEmptyListWhenNoTasks() throws Exception {
    when(taskService.getAllTasksByUserId(1L)).thenReturn(Collections.emptyList());

    mockMvc
        .perform(get("/api/tasks").with(authentication(createAuthentication())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }
}
