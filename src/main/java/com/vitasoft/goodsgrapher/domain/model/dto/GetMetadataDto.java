package com.vitasoft.goodsgrapher.domain.model.dto;

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
    private final String registrationNumber;
    private String lastRightHolderName;
    private String confidence;
    private int workedCount;
    private LocalDateTime reserveDate;
    private String status;
    private GetDesignInfoDto designInfo;
    private int currentWorkerCount;

    public GetMetadataDto(ModelInfo modelInfo) {
        this.modelSeq = modelInfo.getModelSeq();
        this.productCategory = modelInfo.getProductCategory();
        this.articleName = modelInfo.getArticleName();
        this.modelName = modelInfo.getModelName();
        this.companyName = modelInfo.getCompanyName();
        this.regId = modelInfo.getRegId();
        this.regDate = modelInfo.getRegDate();
        this.pathImg = modelInfo.getPathImgGoods();
        this.registrationNumber = modelInfo.getRegistrationNumber();
    }

    public GetMetadataDto(ModelInfo modelInfo, GetDesignInfoDto designInfo, int currentWorkerCount) {
        this(modelInfo);
        this.designInfo = designInfo;
        this.currentWorkerCount = currentWorkerCount;
    }

    public GetMetadataDto(ModelInfo modelInfo, GetDesignInfoDto designInfo, int workedCount, LocalDateTime reserveDate, String status) {
        this(modelInfo);
        this.workedCount = workedCount;
        this.reserveDate = reserveDate;
        this.status = status;
        this.designInfo = designInfo;
    }

    public GetMetadataDto(ModelInfo modelInfo, String type, GetImageSearchDto getImageSearchDto, String confidence, int currentWorkerCount) {
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
        this.currentWorkerCount = currentWorkerCount;
    }

    public GetMetadataDto(ModelInfo modelInfo, String type, GetImageSearchDto getImageSearchDto, int currentWorkerCount) {
        this.modelSeq = modelInfo.getModelSeq();
        this.productCategory = modelInfo.getProductCategory();
        this.articleName = modelInfo.getArticleName();
        this.modelName = modelInfo.getModelName();
        this.companyName = modelInfo.getCompanyName();
        this.regId = modelInfo.getRegId();
        this.regDate = modelInfo.getRegDate();
        this.pathImg = getImageSearchDto.getImgPath();
        this.type = type;
        this.lastRightHolderName = getImageSearchDto.getLastRightHolderName();
        this.registrationNumber = getImageSearchDto.getRegistrationNumber();
        this.currentWorkerCount = currentWorkerCount;
    }

}
