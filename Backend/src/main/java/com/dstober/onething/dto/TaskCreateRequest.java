package com.dstober.onething.dto;

import com.dstober.onething.model.Frequency;
import com.dstober.onething.model.TimeBracket;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TaskCreateRequest {

  @NotBlank(message = "Task name is required")
  private String name;

  @NotNull(message = "Category ID is required")
  private Long categoryId;

  @NotNull(message = "Time bracket is required")
  private TimeBracket timeBracket;

  @NotNull(message = "Frequency is required")
  private Frequency frequency;

  private Long parentId;

  public TaskCreateRequest() {}

  public TaskCreateRequest(
      String name, Long categoryId, TimeBracket timeBracket, Frequency frequency, Long parentId) {
    this.name = name;
    this.categoryId = categoryId;
    this.timeBracket = timeBracket;
    this.frequency = frequency;
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

  public Frequency getFrequency() {
    return frequency;
  }

  public void setFrequency(Frequency frequency) {
    this.frequency = frequency;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }
}
