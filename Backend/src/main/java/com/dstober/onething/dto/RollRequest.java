package com.dstober.onething.dto;

import com.dstober.onething.model.Category;
import com.dstober.onething.model.TimeBracket;

public record RollRequest(
    Category category, TimeBracket timeBracket, RollPreference rollTemperature) {}
