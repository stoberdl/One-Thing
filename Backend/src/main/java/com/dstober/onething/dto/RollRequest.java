package com.dstober.onething.dto;

import com.dstober.onething.model.TimeBracket;
import jakarta.validation.constraints.NotNull;

public record RollRequest(
    Long categoryId, @NotNull TimeBracket timeBracket, @NotNull RollPreference rollTemperature) {}
