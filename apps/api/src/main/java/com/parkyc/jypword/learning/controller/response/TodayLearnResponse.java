package com.parkyc.jypword.learning.controller.response;

import com.parkyc.jypword.learning.service.result.TodayWordResult;

import java.time.LocalDate;
import java.util.List;

public record TodayLearnResponse(
        String member,
        LocalDate learningDate,
        Long wordBookId,
        String wordBookName,
        int cursor,
        List<Word> words
) {
    /** 금일 학습할 단어 목록 리턴 */

    public static TodayLearnResponse from(TodayWordResult result) {
        return new TodayLearnResponse(
                result.member(),
                result.learningDate(),
                result.wordBookId(),
                result.wordBookName(),
                result.cursor(),
                result.words().stream()
                        .map(word -> new Word(
                                word.word(),
                                word.accent(),
                                word.meanings().stream()
                                        .map(meaning -> new Meaning(
                                                meaning.type(),
                                                meaning.meaning(),
                                                meaning.displayOrder()
                                        ))
                                        .toList(),
                                word.sentences().stream()
                                        .map(sentence -> new Sentence(
                                                sentence.sentence(),
                                                sentence.translation(),
                                                sentence.displayOrder()
                                        ))
                                        .toList()
                        ))
                        .toList()
        );
    }

    public record Word(
            String word,
            String accent,
            List<Meaning> meanings,
            List<Sentence> sentences
    ) {
    }

    public record Meaning(String type, String meaning, int displayOrder) {
    }

    public record Sentence(String sentence, String translation, int displayOrder) {
    }
}
