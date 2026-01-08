package com.dstober.onething.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Frequency {
  WEEKLY(7),
  BIWEEKLY(14),
  MONTHLY(30),
  QUARTERLY(90),
  BIANNUALLY(180),
  YEARLY(365);

  private final int days;

  Frequency(int days) {
    this.days = days;
  }

  public int getDays() {
    return days;
  }

  @JsonValue
  public String getValue() {
    return name();
  }

  @JsonCreator
  public static Frequency fromValue(String value) {
    for (Frequency frequency : Frequency.values()) {
      if (frequency.name().equalsIgnoreCase(value)) {
        return frequency;
      }
    }
    throw new IllegalArgumentException("Invalid frequency: " + value);
  }
}
