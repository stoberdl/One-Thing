package com.dstober.onething.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "icon", nullable = false)
  private String icon;

  @Column(name = "color", nullable = false)
  private String color;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  public Category() {}

  public Category(String name, String icon, String color, Long userId) {
    this.name = name;
    this.icon = icon;
    this.color = color;
    this.userId = userId;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getIcon() {
    return icon;
  }

  public String getColor() {
    return color;
  }

  public Long getUserId() {
    return userId;
  }
}
