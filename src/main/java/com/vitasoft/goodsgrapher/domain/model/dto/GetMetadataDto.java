package com.vitasoft.goodsgrapher.domain.model.dto;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Metadata;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class GetMetadataDto {
    private String type;
    private final int metaSeq;
    private final String productCategory;
    private final String articleName;
    private final String modelName;
    private final String companyName;
    private final String registrationNumber;
    private final String applicationNumber;
    private final String lastRightHolderName;
    private final String pathImg;
    private final String reserveId;
    private final LocalDateTime reserveDate;
    private final String regId;
    private final LocalDateTime regDate;
    private int imgCount;

    public GetMetadataDto(Metadata metadata) {
        this.metaSeq = metadata.getMetaSeq();
        this.productCategory = metadata.getProductCategory();
        this.articleName = metadata.getArticleName();
        this.modelName = metadata.getModelName();
        this.companyName = metadata.getCompanyName();
        this.registrationNumber = metadata.getRegistrationNumber();
        this.applicationNumber = metadata.getApplicationNumber();
        this.lastRightHolderName = metadata.getLastRightHolderName();
        this.pathImg = metadata.getPathImg();
        this.reserveId = metadata.getReserveId();
        this.reserveDate = metadata.getReserveDate();
        this.regId = metadata.getRegId();
        this.regDate = metadata.getRegDate();
        this.imgCount = metadata.getImgCount();
    }

    public GetMetadataDto(Metadata metadata, String type) {
        this(metadata);
        this.type = type;
    }

    public GetMetadataDto(Metadata metadata, int count) {
        this(metadata);
        this.imgCount = count;
    }
}
