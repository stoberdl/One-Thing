package com.dstober.onething.controller;

import com.dstober.onething.dto.CategoryCreateRequest;
import com.dstober.onething.dto.TaskCreateRequest;
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
  public ResponseEntity<Task> createTask(
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
  //  @GetMapping("/rng")
  //  public ResponseEntity<Task> determineTaskForUser(@RequestParam Long userId) { // todo: change
  // this
  //    Task task = rollService.determineTaskForUser(userId);
  //    return ResponseEntity.ok(task);
  //  }
  //

  //    @PutMapping("/{id}")
  //    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
  //        Task updated = taskService.updateTask(id, task);
  //        return ResponseEntity.ok(updated);
  //    }

  //    @DeleteMapping("/{id}")
  //    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
  //        taskService.deleteTask(id);
  //        return ResponseEntity.noContent().build();
  //    }
}
