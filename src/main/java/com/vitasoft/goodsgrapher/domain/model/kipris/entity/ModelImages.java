package com.vitasoft.goodsgrapher.domain.model.kipris.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "model_images")
@Data
@DynamicUpdate
@RequiredArgsConstructor
public class ModelImages {
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

    private String articleName;

    private String classCodeKr;

    private String classCodeInt;

    private String angle;

    private String illuminance;

    private String designAssociativity;

    private String obtainInfo;

    private String relevance;

    private String etc1;

    private String etc2;
}
