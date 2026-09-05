package com.parkyc.jypword.wordbook.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "word_book")
@Entity
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(
        name = "SEQ_WORD_BOOK",
        sequenceName = "SEQ_WORD_BOOK",
        allocationSize = 1,
        initialValue = 1
)
public class WordBook {

    @Id
    @Column(name = "word_book_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_WORD_BOOK")
    private Long wordBookId;

    @Column(name = "word_book_name")
    private String wordBookName;

    @OneToMany(mappedBy = "wordBook")
    private List<WordBookItem> words;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Instant updatedAt;
}
