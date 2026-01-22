package com.dstober.onething.dto;

import com.dstober.onething.model.Frequency;
import com.dstober.onething.model.TimeBracket;
import jakarta.validation.constraints.Size;

public class TaskPatchRequest {

  @Size(min = 1, max = 255, message = "Task name must be between 1 and 255 characters")
  private String name;

  private Long categoryId;
  private TimeBracket timeBracket;
  private Frequency frequency;
  private Long parentId;

  public TaskPatchRequest() {}

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
