package com.dstober.onething.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class TimeBracketTest {

    @Test
    void fromValue_UnderFifteen() {
        TimeBracket result = TimeBracket.fromValue("<15");

        assertThat(result).isEqualTo(TimeBracket.UNDER_FIFTEEN);
    }

    @Test
    void fromValue_FifteenToThirty() {
        TimeBracket result = TimeBracket.fromValue("15-30");

        assertThat(result).isEqualTo(TimeBracket.FIFTEEN_TO_THIRTY);
    }

    @Test
    void fromValue_OverThirty() {
        TimeBracket result = TimeBracket.fromValue("30+");

        assertThat(result).isEqualTo(TimeBracket.OVER_THIRTY);
    }

    @Test
    void fromValue_Invalid() {
        assertThatThrownBy(() -> TimeBracket.fromValue("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid time bracket: invalid");
    }

    @Test
    void fromValue_Null() {
        // Null value will cause NullPointerException when comparing with enum values
        assertThatThrownBy(() -> TimeBracket.fromValue(null))
                .isInstanceOf(Exception.class); // Can be NPE or IllegalArgumentException
    }

    @Test
    void fromValue_EmptyString() {
        assertThatThrownBy(() -> TimeBracket.fromValue(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getValue_UnderFifteen() {
        assertThat(TimeBracket.UNDER_FIFTEEN.getValue()).isEqualTo("<15");
    }

    @Test
    void getValue_FifteenToThirty() {
        assertThat(TimeBracket.FIFTEEN_TO_THIRTY.getValue()).isEqualTo("15-30");
    }

    @Test
    void getValue_OverThirty() {
        assertThat(TimeBracket.OVER_THIRTY.getValue()).isEqualTo("30+");
    }

    @Test
    void enumValues_ContainsAllExpected() {
        TimeBracket[] values = TimeBracket.values();

        assertThat(values).hasSize(3);
        assertThat(values).containsExactlyInAnyOrder(
                TimeBracket.UNDER_FIFTEEN,
                TimeBracket.FIFTEEN_TO_THIRTY,
                TimeBracket.OVER_THIRTY
        );
    }
}
