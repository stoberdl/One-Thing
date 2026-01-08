package com.dstober.onething.model;

import jakarta.persistence.*;
import java.time.Instant;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "tasks")
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(name = "category_id", nullable = false)
  private Long categoryId;

  @Column(name = "time_bracket", nullable = false)
  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private TimeBracket timeBracket;

  @Column(nullable = false)
  private Integer priority = 0;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private Frequency frequency;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "last_completed")
  private Instant lastCompleted;

  @Column(name = "prev_completed")
  private Instant prevCompleted;

  @Column(name = "parent_id")
  private Long parentId;

  public Task() {}

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public Frequency getFrequency() {
    return frequency;
  }

  public void setFrequency(Frequency frequency) {
    this.frequency = frequency;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getLastCompleted() {
    return lastCompleted;
  }

  public void setLastCompleted(Instant lastCompleted) {
    this.lastCompleted = lastCompleted;
  }

  public Instant getPrevCompleted() {
    return prevCompleted;
  }

  public void setPrevCompleted(Instant prevCompleted) {
    this.prevCompleted = prevCompleted;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }
}
