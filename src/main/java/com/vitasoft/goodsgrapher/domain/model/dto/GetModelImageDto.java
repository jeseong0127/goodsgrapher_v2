package com.vitasoft.goodsgrapher.domain.model.dto;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelImages;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class GetModelImageDto {
    private final int uploadSeq;
    private final int modelSeq;
    private final int designImgSeq;
    private final int displayOrder;
    private final String displayName;
    private final String fileName;
    private final String fileSize;
    private final String fileType;
    private final String isDeleted;
    private final String registrationNumber;
    private final String brandCode;
    private final String regId;
    private final LocalDateTime regDate;
    private final char inspectPf;
    private final String viewPoint;
    private final String angle;
    private final String illuminance;
    private final String designAssociativity;
    private final String obtainInfo;
    private final String relevance;
    private final String realPath;

    public GetModelImageDto(ModelImages modelImages) {
        this.uploadSeq = modelImages.getUploadSeq();
        this.modelSeq = modelImages.getModelSeq();
        this.designImgSeq = modelImages.getDesignImgSeq();
        this.displayOrder = modelImages.getDisplayOrder();
        this.displayName = modelImages.getDisplayName();
        this.fileName = modelImages.getFileName();
        this.fileSize = modelImages.getFileSize();
        this.fileType = modelImages.getFileType();
        this.isDeleted = modelImages.getIsDeleted();
        this.registrationNumber = modelImages.getRegistrationNumber();
        this.brandCode = modelImages.getBrandCode();
        this.regId = modelImages.getRegId();
        this.regDate = modelImages.getRegDate();
        this.inspectPf = modelImages.getInspectPf();
        this.viewPoint = modelImages.getViewpoint();
        this.angle = modelImages.getAngle();
        this.illuminance = modelImages.getIlluminance();
        this.designAssociativity = modelImages.getDesignAssociativity();
        this.obtainInfo = modelImages.getObtainInfo();
        this.relevance = modelImages.getRelevance();
        this.realPath = modelImages.getRealPath();
    }
}
