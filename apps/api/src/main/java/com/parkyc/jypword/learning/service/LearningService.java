package com.parkyc.jypword.learning.service;

import com.parkyc.jypword.learning.domain.Learning;
import com.parkyc.jypword.learning.repository.LearningJpaRepository;
import com.parkyc.jypword.learning.service.result.TodayWordResult;
import com.parkyc.jypword.wordbook.domain.WordBookItem;
import com.parkyc.jypword.wordbook.domain.WordBook;
import com.parkyc.jypword.wordbook.repository.WordBookJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.List;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class LearningService {
    private static final int TODAY_WORD_COUNT = 20;

    private final LearningJpaRepository learningRepository;
    private final WordBookJpaRepository wordBookRepository;

    @Transactional
    public Optional<TodayWordResult> getTodayWordsByMember(String member) {
        ZoneId zoneId = ZoneId.of("Asia/Seoul");
        Instant now = Instant.now();

        return learningRepository.findCurrentByMember(member)
                .or(() -> createRandomLearning(member))
                .flatMap(learning -> findTodayWords(learning, now, zoneId));
    }

    private Optional<Learning> createRandomLearning(String member) {
        List<WordBook> wordBooks = wordBookRepository.findHavingAtLeastItems(TODAY_WORD_COUNT);
        if (wordBooks.isEmpty()) {
            return Optional.empty();
        }

        WordBook wordBook = wordBooks.get(ThreadLocalRandom.current().nextInt(wordBooks.size()));
        List<WordBookItem> items = wordBookRepository
                .findItemsByWordBookId(wordBook.getWordBookId());
        int startIndex = ThreadLocalRandom.current().nextInt(items.size() - TODAY_WORD_COUNT + 1);
        int cursor = items.get(startIndex).getSequence();

        return Optional.of(learningRepository.save(Learning.start(member, wordBook, cursor)));
    }

    private Optional<TodayWordResult> findTodayWords(Learning learning, Instant now, ZoneId zoneId) {
        int cursor = learning.cursorFor(now, zoneId);
        List<WordBookItem> allItems = wordBookRepository
                .findItemsByWordBookId(learning.getWordBook().getWordBookId());
        if (allItems.size() < TODAY_WORD_COUNT) {
            return Optional.empty();
        }

        int startIndex = indexOfCursor(allItems, cursor);
        if (startIndex < 0 && cursor > allItems.getLast().getSequence()) {
            startIndex = 0;
            cursor = allItems.getFirst().getSequence();
        } else if (startIndex < 0) {
            return Optional.empty();
        }

        int finalStartIndex = startIndex;
        List<WordBookItem> todayItems = IntStream.range(0, TODAY_WORD_COUNT)
                .mapToObj(offset -> allItems.get((finalStartIndex + offset) % allItems.size()))
                .toList();
        return Optional.of(toResult(
                learning,
                todayItems,
                now.atZone(zoneId).toLocalDate(),
                cursor
        ));
    }

    private int indexOfCursor(List<WordBookItem> items, int cursor) {
        for (int index = 0; index < items.size(); index++) {
            if (items.get(index).getSequence() == cursor) {
                return index;
            }
        }
        return -1;
    }

    private TodayWordResult toResult(
            Learning learning,
            List<WordBookItem> items,
            LocalDate learningDate,
            int cursor
    ) {
        return new TodayWordResult(
                learning.getMember(),
                learningDate,
                learning.getWordBook().getWordBookId(),
                learning.getWordBook().getWordBookName(),
                cursor,
                items.stream()
                        .map(item -> new TodayWordResult.WordResult(
                                item.getWord().getWord(),
                                item.getWord().getAccent(),
                                item.getWord().getWordMeans().stream()
                                        .sorted(Comparator.comparingInt(mean -> mean.getDisplayOrder()))
                                        .map(mean -> new TodayWordResult.MeaningResult(
                                                mean.getMeanType().name(),
                                                mean.getMeaning(),
                                                mean.getDisplayOrder()
                                        ))
                                        .toList(),
                                item.getWord().getWordSentences().stream()
                                        .sorted(Comparator.comparingInt(sentence -> sentence.getDisplayOrder()))
                                        .map(sentence -> new TodayWordResult.SentenceResult(
                                                sentence.getSentence(),
                                                sentence.getSentenceMean(),
                                                sentence.getDisplayOrder()
                                        ))
                                        .toList()
                        ))
                        .toList()
        );
    }
}
