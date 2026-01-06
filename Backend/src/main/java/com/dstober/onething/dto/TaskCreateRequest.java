package com.dstober.onething.dto;

import com.dstober.onething.model.TimeBracket;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TaskCreateRequest {

  @NotBlank(message = "Task name is required")
  private String name;

  @NotNull(message = "Category ID is required")
  private Long categoryId;

  @NotNull(message = "Time bracket is required")
  private TimeBracket timeBracket;

  @NotNull(message = "Priority is required")
  @Min(value = 1, message = "Priority must be at least 1")
  @Max(value = 3, message = "Priority must be at most 3")
  private Integer priority;

  private Long parentId;

  public TaskCreateRequest() {}

  public TaskCreateRequest(
      String name, Long categoryId, TimeBracket timeBracket, Integer priority, Long parentId) {
    this.name = name;
    this.categoryId = categoryId;
    this.timeBracket = timeBracket;
    this.priority = priority;
    this.parentId = parentId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public TimeBracket getTimeBracket() {
    return timeBracket;
  }

  public void setTimeBracket(TimeBracket timeBracket) {
    this.timeBracket = timeBracket;
  }

  public Integer getPriority() {
    return priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }
}
