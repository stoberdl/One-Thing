package com.dstober.onething.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record RollPreference(@Min(0) @Max(1) double priorityToRandomness) {}
