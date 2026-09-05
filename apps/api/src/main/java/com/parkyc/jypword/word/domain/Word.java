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
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "word")
@Entity
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(
        name = "SEQ_WORD",
        sequenceName = "SEQ_WORD",
        allocationSize = 1,
        initialValue = 1
)
public class Word {

    @Id
    @Column(name = "word_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_WORD")
    private Long wordId;

    @Column(name = "word")
    private String word;

    @Column(name = "accent")
    private String accent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "word")
    private List<WordMean> wordMeans;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "word")
    private List<WordSentence> wordSentences;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private WordStatus status;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Instant updatedAt;
}


