package com.vitasoft.goodsgrapher.domain.model.dto;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelImage;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import org.json.simple.JSONObject;

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
    private final JSONObject imageJson;

    public GetModelImageDto(ModelImage modelImage) {
        this.uploadSeq = modelImage.getUploadSeq();
        this.modelSeq = modelImage.getModelSeq();
        this.designImgSeq = modelImage.getDesignImgSeq();
        this.displayOrder = modelImage.getDisplayOrder();
        this.displayName = modelImage.getDisplayName();
        this.fileName = modelImage.getFileName();
        this.fileSize = modelImage.getFileSize();
        this.fileType = modelImage.getFileType();
        this.isDeleted = modelImage.getIsDeleted();
        this.registrationNumber = modelImage.getRegistrationNumber();
        this.brandCode = modelImage.getBrandCode();
        this.regId = modelImage.getRegId();
        this.regDate = modelImage.getRegDate();
        this.inspectPf = modelImage.getInspectPf();
        this.viewPoint = modelImage.getViewpoint();
        this.angle = modelImage.getAngle();
        this.illuminance = modelImage.getIlluminance();
        this.designAssociativity = modelImage.getDesignAssociativity();
        this.obtainInfo = modelImage.getObtainInfo();
        this.relevance = modelImage.getRelevance();
        this.imageJson = modelImage.getImageJson();
    }
}
