package com.parkyc.jypword.learning.controller;

import com.parkyc.jypword.learning.controller.request.TodayLearnRequest;
import com.parkyc.jypword.learning.controller.response.TodayLearnResponse;
import com.parkyc.jypword.learning.service.LearningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/word/learn")
@RequiredArgsConstructor
public class LearningController {
    /** 사용자 주로 사용하는 API */

    private final LearningService learningService;

    // 1. 금일 학습목록 가져오기.
    @GetMapping("/today")
    public ResponseEntity<TodayLearnResponse> getTodayLearnWords(@Valid TodayLearnRequest request) {
        String member = request.getMember() != null
                ? request.getMember()
                : UUID.randomUUID().toString();

        return learningService.getTodayWordsByMember(member)
                .map(TodayLearnResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    // 2. 학습 내용 저장하기.
}
