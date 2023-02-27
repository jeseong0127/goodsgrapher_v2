package com.vitasoft.goodsgrapher.domain.model.kipris.entity;

import com.vladmihalcea.hibernate.type.json.JsonStringType;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.json.simple.JSONObject;

@Entity
@Table(name = "meta_model_images")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Data
@DynamicUpdate
@DynamicInsert
@RequiredArgsConstructor
public class ModelImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uploadSeq;

    private int modelSeq;

    private int designImgSeq;

    private int displayOrder;

    private String displayName;

    private String fileName;

    private String fileSize;

    private String fileType;

    private String isDeleted;

    private String registrationNumber;

    private String brandCode;

    private String regId;

    private LocalDateTime regDate;

    private char inspectPf;

    private String viewpoint;

    private String angle;

    private String illuminance;

    private String designAssociativity;

    private String obtainInfo;

    private String relevance;

    private String etc1;

    private String etc2;

    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    private JSONObject imageJson;

    public ModelImage(String memberId, ModelInfo metadata, String fileName, String fileSize, String fileType, int displayOrder, String brandCode, String viewpoint, JSONObject imageJson) {
        this.modelSeq = metadata.getModelSeq();
        this.designImgSeq = 0;
        this.displayOrder = displayOrder;
        this.displayName = fileName;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.registrationNumber = metadata.getRegistrationNumber();
        this.brandCode = brandCode;
        this.regId = memberId;
        this.regDate = LocalDateTime.now();
        this.inspectPf = 'N';
        this.viewpoint = viewpoint;
        this.imageJson = imageJson;
    }
}
