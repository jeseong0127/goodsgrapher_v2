package com.vitasoft.goodsgrapher.domain.model.dto;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignInfo;
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
    private final String pathImg;
    private DesignInfo designInfo;

    public GetMetadataDto(ModelInfo modelInfo) {
        this.modelSeq = modelInfo.getModelSeq();
        this.productCategory = modelInfo.getProductCategory();
        this.articleName = modelInfo.getArticleName();
        this.modelName = modelInfo.getModelName();
        this.companyName = modelInfo.getCompanyName();
        this.regId = modelInfo.getRegId();
        this.regDate = modelInfo.getRegDate();
        this.pathImg = modelInfo.getPathImgGoods();
        this.designInfo = modelInfo.getDesignInfo();
    }

    public GetMetadataDto(ModelInfo modelInfo, String type) {
        this.modelSeq = modelInfo.getModelSeq();
        this.productCategory = modelInfo.getProductCategory();
        this.articleName = modelInfo.getArticleName();
        this.modelName = modelInfo.getModelName();
        this.companyName = modelInfo.getCompanyName();
        this.regId = modelInfo.getRegId();
        this.regDate = modelInfo.getRegDate();
        this.pathImg = modelInfo.getPathImgGoods();
        this.type = type;
    }

    public GetMetadataDto(ModelInfo modelInfo, String type, String imgPath) {
        this.modelSeq = modelInfo.getModelSeq();
        this.productCategory = modelInfo.getProductCategory();
        this.articleName = modelInfo.getArticleName();
        this.modelName = modelInfo.getModelName();
        this.companyName = modelInfo.getCompanyName();
        this.regId = modelInfo.getRegId();
        this.regDate = modelInfo.getRegDate();
        this.pathImg = imgPath;
        this.type = type;
    }
}
