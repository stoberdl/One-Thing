package com.dstober.onething.service;

import com.dstober.onething.dto.TaskCreateRequest;
import com.dstober.onething.exception.ResourceNotFoundException;
import com.dstober.onething.model.Task;
import com.dstober.onething.repository.TaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

  private final TaskRepository taskRepository;

  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public Task createTask(TaskCreateRequest request, Long authenticatedUserId) {
    Task task = new Task();
    task.setName(request.getName());
    task.setCategoryId(request.getCategoryId());
    task.setTimeBracket(request.getTimeBracket());
    task.setFrequency(request.getFrequency());
    task.setParentId(request.getParentId());
    task.setUserId(authenticatedUserId);

    return taskRepository.save(task);
  }

  public Task getTaskByIdAndUserId(Long id, Long userId) {
    Task task =
        taskRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

    if (!task.getUserId().equals(userId)) {
      throw new ResourceNotFoundException("Task not found with id: " + id);
    }

    return task;
  }

  public List<Task> getAllTasksByUserId(Long userId) {
    return taskRepository.findByUserId(userId);
  }

  public Task updateTask(Long id, Task taskDetails, Long userId) {
    Task existingTask = getTaskByIdAndUserId(id, userId);

    existingTask.setName(taskDetails.getName());
    existingTask.setCategoryId(taskDetails.getCategoryId());
    existingTask.setTimeBracket(taskDetails.getTimeBracket());
    existingTask.setFrequency(taskDetails.getFrequency());
    existingTask.setLastCompleted(taskDetails.getLastCompleted());
    existingTask.setPrevCompleted(taskDetails.getPrevCompleted());
    existingTask.setParentId(taskDetails.getParentId());
    // priority is not updated directly - it's recalculated on login

    return taskRepository.save(existingTask);
  }

  public void deleteTask(Long id, Long userId) {
    Task task = getTaskByIdAndUserId(id, userId);
    taskRepository.delete(task);
  }
}
