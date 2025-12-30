package com.dstober.onething.controller;

import com.dstober.onething.model.Task;
import com.dstober.onething.service.RollService;
import com.dstober.onething.service.TaskService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private final TaskService taskService;
  private final RollService rollService;

  public TaskController(TaskService taskService, RollService rollService) {
    this.taskService = taskService;
    this.rollService = rollService;
  }

  @PostMapping
  public ResponseEntity<Task> createTask(@RequestBody Task task) {
    Task created = taskService.createTask(task);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  //    @GetMapping("/{id}")
  //    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
  //        Task task = taskService.getTaskByIdAndUserId(id);
  //        return ResponseEntity.ok(task);
  //    }
  @GetMapping("/rng")
  public ResponseEntity<Task> determineTaskForUser(@RequestParam Long userId) { // todo: change this
    Task task = rollService.determineTaskForUser(userId);
    return ResponseEntity.ok(task);
  }

  @GetMapping
  public ResponseEntity<List<Task>> getTasksByUserId(@RequestParam Long userId) {
    List<Task> tasks = taskService.getAllTasksByUserId(userId);
    return ResponseEntity.ok(tasks);
  }

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
