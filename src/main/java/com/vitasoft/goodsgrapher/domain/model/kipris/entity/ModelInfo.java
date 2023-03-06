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
@Table(name = "meta_model_info")
@Data
@DynamicUpdate
@RequiredArgsConstructor
public class ModelInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int modelSeq;

    private String registrationNumber;

    private String highProductCategory;

    private String highCodeId;

    private String productCategory;

    private String codeId;

    private String articleName;

    private String modelName;

    private String companyName;

    private String pathImgGoods;

    private String regId;

    private LocalDateTime regDate;

    private String updId;

    private LocalDateTime updDate;

    private String delId;

    private LocalDateTime delDate;

    private char useYn;
}
