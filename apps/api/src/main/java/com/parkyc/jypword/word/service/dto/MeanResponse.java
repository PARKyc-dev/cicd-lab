package com.parkyc.jypword.word.service.dto;

import com.parkyc.jypword.word.domain.MeanType;
import lombok.Data;

@Data
public class MeanResponse {
    private MeanType type;

    private String mean;

    private int displayOrder;
}
