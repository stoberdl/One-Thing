package com.dstober.onething.controller;

import com.dstober.onething.dto.CategoryCreateRequest;
import com.dstober.onething.dto.RollRequest;
import com.dstober.onething.dto.TaskCreateRequest;
import com.dstober.onething.dto.TaskPatchRequest;
import com.dstober.onething.model.Category;
import com.dstober.onething.model.Task;
import com.dstober.onething.security.UserPrincipal;
import com.dstober.onething.service.CategoryService;
import com.dstober.onething.service.RollService;
import com.dstober.onething.service.TaskService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;
  private final RollService rollService;
  private final CategoryService categoryService;

  public TaskController(
      TaskService taskService, RollService rollService, CategoryService categoryService) {
    this.taskService = taskService;
    this.rollService = rollService;
    this.categoryService = categoryService;
  }

  @PostMapping
  public ResponseEntity<Task> createTask( // todo:better validation error response, for bad data
      @Valid @RequestBody TaskCreateRequest request, Authentication authentication) {
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    Task created = taskService.createTask(request, principal.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PostMapping("/categories")
  public ResponseEntity<Category> createCategory(
      @Valid @RequestBody CategoryCreateRequest request, Authentication authentication) {
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    Category created = categoryService.createCategory(request, principal.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping
  public ResponseEntity<List<Task>> getAllTasksForUser(Authentication authentication) {
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    List<Task> tasks = taskService.getAllTasksByUserId(principal.getId());
    return ResponseEntity.ok(tasks);
  }

  // todo:auth at top keyword?
  @PostMapping("/roll")
  public ResponseEntity<Task> determineTaskForUser(
      Authentication authentication, @Valid @RequestBody RollRequest rollRequest) {
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    Task task = rollService.determineTaskForUser(principal.getId(), rollRequest);
    return ResponseEntity.ok(task);
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<Void> deleteTask(
      @PathVariable("taskId") Long taskId, Authentication authentication) {
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    taskService.deleteTask(taskId, principal.getId());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PatchMapping("/{taskId}")
  public ResponseEntity<Task> patchTask(
      @PathVariable Long taskId,
      @Valid @RequestBody TaskPatchRequest request,
      Authentication authentication) {
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    Task created = taskService.patchTask(taskId, request, principal.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }
}
