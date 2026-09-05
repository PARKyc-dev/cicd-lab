package com.parkyc.jypword.word.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class WordResponse {

    private String word;

    private String accent;

    private List<MeanResponse> means;

    private List<SentenceResponse> sentences;
}
