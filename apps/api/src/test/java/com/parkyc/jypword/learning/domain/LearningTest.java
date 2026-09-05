package com.parkyc.jypword.learning.domain;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class LearningTest {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    @Test
    void returnsCurrentCursorWhenAlreadyLearnedToday() {
        Learning learning = learning(3, Instant.parse("2026-09-02T00:00:00Z"));

        int cursor = learning.cursorFor(Instant.parse("2026-09-02T10:00:00Z"), SEOUL);

        assertThat(cursor).isEqualTo(3);
    }

    @Test
    void returnsNextCursorOnFollowingDay() {
        Learning learning = learning(3, Instant.parse("2026-09-01T10:00:00Z"));

        int cursor = learning.cursorFor(Instant.parse("2026-09-02T00:00:00Z"), SEOUL);

        assertThat(cursor).isEqualTo(4);
    }

    @Test
    void firstLearningKeepsStartingCursor() {
        Learning learning = learning(3, null);

        int cursor = learning.cursorFor(Instant.parse("2026-09-02T00:00:00Z"), SEOUL);

        assertThat(cursor).isEqualTo(3);
    }

    private Learning learning(int currentCursor, Instant lastLearnedAt) {
        Learning learning = new Learning();
        ReflectionTestUtils.setField(learning, "currentCursor", currentCursor);
        ReflectionTestUtils.setField(learning, "lastLearnedAt", lastLearnedAt);
        return learning;
    }
}
