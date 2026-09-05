package com.parkyc.jypword.learning.service;

import com.parkyc.jypword.learning.domain.Learning;
import com.parkyc.jypword.learning.repository.LearningJpaRepository;
import com.parkyc.jypword.learning.service.result.TodayWordResult;
import com.parkyc.jypword.word.domain.Word;
import com.parkyc.jypword.word.domain.WordStatus;
import com.parkyc.jypword.wordbook.domain.WordBook;
import com.parkyc.jypword.wordbook.domain.WordBookItem;
import com.parkyc.jypword.wordbook.repository.WordBookJpaRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LearningServiceTest {

    @Test
    void createsRandomLearningAndReturnsTwentyWordsWhenLearningDoesNotExist() {
        LearningJpaRepository learningRepository = mock(LearningJpaRepository.class);
        WordBookJpaRepository wordBookRepository = mock(WordBookJpaRepository.class);
        LearningService service = new LearningService(
                learningRepository,
                wordBookRepository
        );
        WordBook wordBook = new WordBook(1L, "sample", null, null, null);
        List<WordBookItem> items = items(wordBook, 25);

        when(learningRepository.findCurrentByMember("member-uuid")).thenReturn(Optional.empty());
        when(wordBookRepository.findHavingAtLeastItems(20)).thenReturn(List.of(wordBook));
        when(wordBookRepository.findItemsByWordBookId(1L)).thenReturn(items);
        when(learningRepository.save(any(Learning.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TodayWordResult result = service.getTodayWordsByMember("member-uuid").orElseThrow();

        assertThat(result.member()).isEqualTo("member-uuid");
        assertThat(result.wordBookId()).isEqualTo(1L);
        assertThat(result.cursor()).isBetween(1, 6);
        assertThat(result.words()).hasSize(20);
        assertThat(result.words().getFirst().word()).isEqualTo("word" + result.cursor());
        verify(learningRepository).save(any(Learning.class));
    }

    @Test
    void wrapsToFirstWordWhenNextCursorPassesEndOfWordBook() {
        LearningJpaRepository learningRepository = mock(LearningJpaRepository.class);
        WordBookJpaRepository wordBookRepository = mock(WordBookJpaRepository.class);
        LearningService service = new LearningService(
                learningRepository,
                wordBookRepository
        );
        WordBook wordBook = new WordBook(1L, "sample", null, null, null);
        Learning learning = Learning.start("member-uuid", wordBook, 25);
        learning.completeWord(25, Instant.now().minusSeconds(172_800));

        when(learningRepository.findCurrentByMember("member-uuid"))
                .thenReturn(Optional.of(learning));
        when(wordBookRepository.findItemsByWordBookId(1L))
                .thenReturn(items(wordBook, 25));

        TodayWordResult result = service.getTodayWordsByMember("member-uuid").orElseThrow();

        assertThat(result.cursor()).isEqualTo(1);
        assertThat(result.words()).hasSize(20);
        assertThat(result.words().getFirst().word()).isEqualTo("word1");
    }

    private List<WordBookItem> items(WordBook wordBook, int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(sequence -> new WordBookItem(
                        (long) sequence,
                        wordBook,
                        new Word(
                                (long) sequence,
                                "word" + sequence,
                                null,
                                List.of(),
                                List.of(),
                                WordStatus.ACTIVE,
                                null,
                                null
                        ),
                        sequence,
                        null,
                        null
                ))
                .toList();
    }
}
