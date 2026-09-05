-- Adds every active word to word_book_id 2 in a randomized order.
-- Existing items for this word book are retained and not duplicated.
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
    2,
    shuffled_words.word_id,
    shuffled_words.sequence,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM (
    SELECT
        word.word_id,
        row_number() OVER (ORDER BY random())
            + COALESCE((
                SELECT max(sequence)
                FROM word_book_item
                WHERE word_book_id = 2
            ), 0) AS sequence
    FROM word
    WHERE word.status = 'ACTIVE'
      AND NOT EXISTS (
          SELECT 1
          FROM word_book_item
          WHERE word_book_item.word_book_id = 2
            AND word_book_item.word_id = word.word_id
      )
) AS shuffled_words;
