package com.parkyc.jypword.wordbook.domain;


import com.parkyc.jypword.word.domain.Word;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "word_book_item")
@Entity
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(
        name = "SEQ_WORD_BOOK_ITEM",
        sequenceName = "SEQ_WORD_BOOK_ITEM",
        allocationSize = 1,
        initialValue = 1
)
public class WordBookItem {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_WORD_BOOK_ITEM")
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_book_id")
    private WordBook wordBook;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    @Column(name = "sequence")
    private int sequence;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Instant updatedAt;
}

