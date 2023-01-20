package com.vitasoft.goodsgrapher.domain.model.dto;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GetMetadataDetailDto {
    private final int modelSeq;
    private final String productCategory;
    private final String articleName;
    private final String modelName;
    private final String companyName;
    private final String regId;
    private final LocalDateTime regDate;
    private final DesignInfo designInfo;

    public GetMetadataDetailDto(ModelInfo modelInfo, DesignInfo designInfo) {
        this.modelSeq = modelInfo.getModelSeq();
        this.productCategory = modelInfo.getProductCategory();
        this.articleName = modelInfo.getArticleName();
        this.modelName = modelInfo.getModelName();
        this.companyName = modelInfo.getCompanyName();
        this.regId = modelInfo.getRegId();
        this.regDate = modelInfo.getRegDate();
        this.designInfo = designInfo;
    }
}
