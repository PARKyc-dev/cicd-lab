package com.parkyc.jypword.word.domain;

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
@Table(name = "word_sentence")
@Entity
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(
        name = "SEQ_W_SENTENCE",
        sequenceName = "SEQ_W_SENTENCE",
        allocationSize = 1,
        initialValue = 1
)
public class WordSentence {

    @Id
    @Column(name = "sentence_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_W_SENTENCE")
    private Long sentenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    @Column(name = "sentence")
    private String sentence;

    @Column(name = "sentence_mean")
    private String sentenceMean;

    @Column(name = "display_order")
    private int displayOrder;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Instant updatedAt;

}
