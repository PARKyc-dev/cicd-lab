package com.parkyc.jypword.learning.controller.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class TodayLearnRequest {

    /* 회원정보를 개발하기 전에 UUID로 대체 */

    @Pattern(
            regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "member는 UUID 형식이어야 합니다."
    )
    private String member;

    // private Member member; // 회원 ID(UUID)를 넣으면 금일 공부할 내용은 리턴한다. 오늘 공부할 내용은 브라우저에 저장하고 사용한다.
}
