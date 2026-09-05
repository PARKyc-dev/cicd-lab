package com.parkyc.jypword.learning.controller;

import com.parkyc.jypword.learning.controller.request.TodayLearnRequest;
import com.parkyc.jypword.learning.controller.response.TodayLearnResponse;
import com.parkyc.jypword.learning.service.LearningService;
import com.parkyc.jypword.learning.service.result.TodayWordResult;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LearningControllerTest {

    @Test
    void createsAnonymousMemberInResponseWhenRequestMemberIsNull() {
        LearningService learningService = mock(LearningService.class);
        when(learningService.getTodayWordsByMember(anyString()))
                .thenAnswer(invocation -> Optional.of(result(invocation.getArgument(0))));
        LearningController controller = new LearningController(learningService);

        ResponseEntity<TodayLearnResponse> response = controller.getTodayLearnWords(new TodayLearnRequest());

        String member = response.getBody().member();
        assertThatCodeIsUuid(member);
        assertThat(response.getBody().words()).hasSize(20);
        assertThat(response.getHeaders().get("Set-Cookie")).isNull();
    }

    private TodayWordResult result(String member) {
        return new TodayWordResult(
                member,
                LocalDate.of(2026, 9, 2),
                1L,
                "sample",
                1,
                IntStream.rangeClosed(1, 20)
                        .mapToObj(index -> new TodayWordResult.WordResult(
                                "word" + index,
                                null,
                                List.of(),
                                List.of()
                        ))
                        .toList()
        );
    }

    private void assertThatCodeIsUuid(String member) {
        assertThat(UUID.fromString(member).toString()).isEqualTo(member);
    }
}
