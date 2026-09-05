package com.parkyc.jypword.learning.service.result;

import java.time.LocalDate;
import java.util.List;

public record TodayWordResult(
        String member,
        LocalDate learningDate,
        Long wordBookId,
        String wordBookName,
        int cursor,
        List<WordResult> words
) {
    public record WordResult(
            String word,
            String accent,
            List<MeaningResult> meanings,
            List<SentenceResult> sentences
    ) {
    }

    public record MeaningResult(String type, String meaning, int displayOrder) {
    }

    public record SentenceResult(String sentence, String translation, int displayOrder) {
    }
}
