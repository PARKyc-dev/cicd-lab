-- Creates one randomized word book from all active words.
-- Re-running this query does nothing after the named word book has been created.

WITH existing_word_book AS (
    SELECT word_book_id
    FROM word_book
    WHERE word_book_name = '랜덤 학습 단어장'
),
created_word_book AS (
    INSERT INTO word_book (word_book_id, word_book_name, created_at, updated_at)
    SELECT nextval('seq_word_book'), '랜덤 학습 단어장', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM existing_word_book)
    RETURNING word_book_id
),
target_word_book AS (
    SELECT word_book_id FROM created_word_book
    UNION ALL
    SELECT word_book_id FROM existing_word_book
),
shuffled_words AS (
    SELECT
        word_id,
        row_number() OVER (ORDER BY random()) AS sequence
    FROM word
    WHERE status = 'ACTIVE'
)
INSERT INTO word_book_item (
    item_id,
    word_book_id,
    word_id,
    sequence,
    created_at,
    updated_at
)
SELECT
    nextval('seq_word_book_item'),
    target_word_book.word_book_id,
    shuffled_words.word_id,
    shuffled_words.sequence,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM target_word_book
CROSS JOIN shuffled_words
WHERE NOT EXISTS (
    SELECT 1
    FROM word_book_item
    WHERE word_book_item.word_book_id = target_word_book.word_book_id
);
