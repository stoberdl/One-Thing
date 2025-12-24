package com.dstober.onething.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TimeBracket {
    UNDER_FIFTEEN("<15"),
    FIFTEEN_TO_THIRTY("15-30"),
    OVER_THIRTY("30+");

    private final String value;

    TimeBracket(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TimeBracket fromValue(String value) {
        for (TimeBracket bracket : TimeBracket.values()) {
            if (bracket.value.equals(value)) {
                return bracket;
            }
        }
        throw new IllegalArgumentException("Invalid time bracket: " + value);
    }
}
