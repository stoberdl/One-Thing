package com.dstober.onething.service;

import com.dstober.onething.exception.ResourceNotFoundException;
import com.dstober.onething.model.Task;
import com.dstober.onething.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
    }

    public List<Task> getAllTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    public Task updateTask(Long id, Task taskDetails) {
        Task existingTask = getTaskById(id);

        existingTask.setName(taskDetails.getName());
        existingTask.setCategoryId(taskDetails.getCategoryId());
        existingTask.setTimeBracket(taskDetails.getTimeBracket());
        existingTask.setPriority(taskDetails.getPriority());
        existingTask.setLastCompleted(taskDetails.getLastCompleted());
        existingTask.setPrevCompleted(taskDetails.getPrevCompleted());
        existingTask.setParentId(taskDetails.getParentId());

        return taskRepository.save(existingTask);
    }

    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }
}
