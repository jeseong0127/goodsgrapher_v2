package com.vitasoft.goodsgrapher.domain.model.dto;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Code;

import lombok.Getter;

@Getter
public class GetCategoryDto {
    private final String highCodeId;
    private final String codeId;
    private final String codeName;

    public GetCategoryDto(Code code) {
        this.highCodeId = code.getHighCodeId();
        this.codeId = code.getCodeId();
        this.codeName = code.getCodeName();
    }
}
