package com.parkyc.jypword.wordbook.repository;

import com.parkyc.jypword.wordbook.domain.WordBook;
import com.parkyc.jypword.wordbook.domain.WordBookItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordBookJpaRepository extends JpaRepository<WordBook, Long> {

    @Query("""
            select wordBook
            from WordBook wordBook
            join wordBook.words item
            group by wordBook
            having count(item) >= :itemCount
            """)
    List<WordBook> findHavingAtLeastItems(@Param("itemCount") long itemCount);

    @Query("""
            select item
            from WordBookItem item
            join fetch item.word
            where item.wordBook.wordBookId = :wordBookId
            order by item.sequence asc
            """)
    List<WordBookItem> findItemsByWordBookId(@Param("wordBookId") Long wordBookId);
}
