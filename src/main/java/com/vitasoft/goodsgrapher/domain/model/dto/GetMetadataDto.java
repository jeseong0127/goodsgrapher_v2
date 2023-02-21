package com.vitasoft.goodsgrapher.domain.model.dto;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
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
    private String registrationNumber;
    private String lastRightHolderName;
    private String confidence;
    private int workedCount;

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

    public GetMetadataDto(ModelInfo modelInfo, int workedCount) {
        this(modelInfo);
        this.workedCount = workedCount;
    }

    public GetMetadataDto(ModelInfo modelInfo, String type, GetImageSearchDto getImageSearchDto, String confidence) {
        this.modelSeq = modelInfo.getModelSeq();
        this.productCategory = modelInfo.getProductCategory();
        this.articleName = modelInfo.getArticleName();
        this.modelName = modelInfo.getModelName();
        this.companyName = modelInfo.getCompanyName();
        this.regId = modelInfo.getRegId();
        this.regDate = modelInfo.getRegDate();
        this.pathImg = getImageSearchDto.getImgPath();
        this.type = type;
        this.confidence = confidence;
        this.lastRightHolderName = getImageSearchDto.getLastRightHolderName();
        this.registrationNumber = getImageSearchDto.getRegistrationNumber();
    }
}
