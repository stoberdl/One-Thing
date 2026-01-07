package com.dstober.onething.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CategoryCreateRequest {

  @NotBlank(message = "Category name is required")
  private String name;

  @NotBlank(message = "Icon is required")
  private String icon;

  @NotBlank(message = "Category color is required")
  @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Invalid hex color format. Use #RRGGBB format")
  private String color;

  public CategoryCreateRequest() {}

  public CategoryCreateRequest(String name, Long categoryId, String icon, String color) {
    this.name = name;
    this.icon = icon;
    this.color = color;
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
}
