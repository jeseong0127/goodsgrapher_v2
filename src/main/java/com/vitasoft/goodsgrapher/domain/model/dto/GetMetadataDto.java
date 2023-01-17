package com.vitasoft.goodsgrapher.domain.model.dto;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class GetMetadataDto {
    private String type;
    private final int modelSeq;
    private final String productCategory;
    private final String articleName;
    private final String modelName;
    private final String companyName;
    private final String regId;
    private final LocalDateTime regDate;
    private int imgCount;

    public GetMetadataDto(ModelInfo metadata) {
        this.modelSeq = metadata.getModelSeq();
        this.productCategory = metadata.getProductCategory();
        this.articleName = metadata.getArticleName();
        this.modelName = metadata.getModelName();
        this.companyName = metadata.getCompanyName();
        this.regId = metadata.getRegId();
        this.regDate = metadata.getRegDate();
    }

    public GetMetadataDto(ModelInfo metadata, String type) {
        this(metadata);
        this.type = type;
    }

    public GetMetadataDto(ModelInfo metadata, int count) {
        this(metadata);
        this.imgCount = count;
    }
}
