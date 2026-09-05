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
@Table(name = "word_mean")
@Entity
@EntityListeners(AuditingEntityListener.class)
@SequenceGenerator(
        name = "SEQ_W_MEAN",
        sequenceName = "SEQ_W_MEAN",
        allocationSize = 1,
        initialValue = 1
)
public class WordMean {

    @Id
    @Column(name = "mean_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_W_MEAN")
    private Long meanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id")
    private Word word;

    @Enumerated(EnumType.STRING)
    @Column(name = "mean_type")
    private MeanType meanType;

    @Column(name = "meaning")
    private String meaning;

    @Column(name = "display_order")
    private int displayOrder;

    @Column(name = "created_at", updatable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Instant updatedAt;
}
