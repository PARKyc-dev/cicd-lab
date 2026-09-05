package com.parkyc.jypword.learning.repository;

import com.parkyc.jypword.learning.domain.Learning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LearningJpaRepository extends JpaRepository<Learning, Long> {

    @Query("""
            select learning
            from Learning learning
            join fetch learning.wordBook
            where learning.member = :member
              and learning.isCompleted = false
            order by learning.createdAt desc
            limit 1
            """)
    Optional<Learning> findCurrentByMember(@Param("member") String member);
}
