package com.parkyc.jypword.learning.domain;

import com.parkyc.jypword.member.domain.Member;
import com.parkyc.jypword.wordbook.domain.WordBook;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Table(name = "learning")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(
        name = "SEQ_LEARNING",
        sequenceName = "SEQ_LEARNING",
        allocationSize = 1,
        initialValue = 1
)
public class Learning {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_LEARNING")
    private Long learningId;


    /* 일단 회원 부분을 삭제하고 uuid로 대체 */
    @Column(name = "member")
    private String member;
    /**
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_book_id")
    private WordBook wordBook;

    @Column(name = "start_cursor")
    private int startCursor;

    @Column(name = "current_cursor")
    private int currentCursor;

    @Column(name = "loop_count", nullable = false)
    private int loopCount;

    @Column(name = "is_completed")
    private boolean isCompleted;

    @Column(name = "completedAt")
    private Instant completedAt;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Instant updatedAt;

    @Column(name = "last_learned_at")
    private Instant lastLearnedAt;

    public static Learning start(String member, WordBook wordBook, int cursor) {
        Learning learning = new Learning();
        learning.member = member;
        learning.wordBook = wordBook;
        learning.startCursor = cursor;
        learning.currentCursor = cursor;
        learning.loopCount = 0;
        return learning;
    }

    public int cursorFor(Instant now, java.time.ZoneId zoneId) {
        if (lastLearnedAt == null) {
            return currentCursor;
        }

        java.time.LocalDate lastLearnedDate = lastLearnedAt.atZone(zoneId).toLocalDate();
        java.time.LocalDate today = now.atZone(zoneId).toLocalDate();
        return lastLearnedDate.isBefore(today) ? currentCursor + 1 : currentCursor;
    }

    public void completeWord(int cursor, Instant learnedAt) {
        this.currentCursor = cursor;
        this.lastLearnedAt = learnedAt;
    }
}
