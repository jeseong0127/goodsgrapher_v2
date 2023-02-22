package com.vitasoft.goodsgrapher.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetDesignImageDto {
    private final int imgNumber;
    private final String imgPath;
    private final String useYn;
    private final int designImgSeq;
}
