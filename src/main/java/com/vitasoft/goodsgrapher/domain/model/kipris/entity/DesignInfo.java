package com.vitasoft.goodsgrapher.domain.model.kipris.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "meta_design_Info")
@Data
@DynamicUpdate
@RequiredArgsConstructor
public class DesignInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int designSeq;

    @OneToOne
    @JoinColumn(name = "DESIGN_SEQ")
    private ModelInfo metadata;

    @ManyToOne
    @JoinColumn(name = "DESIGN_SEQ")
    private DesignImages designImages;

    private String registrationNumber;

    private String designNumber;

    private String applicationNumber;

    private String dsScgrtDmndYn;

    private String kpsArticleName;

    private String kpsCompanyName;

    private String lastRightHolderName;

    private String rightCode;

    private String kpsImgUrl;

    private String imgPath;

    private String kpsPdfUrl;

    private String pdfPath;

    private String pathImgGoods;

    private String dsshpclsscd;

    private String agentName;

    private String agentAddress;

    private String registrationDate;

    private String classCodeInt;

    private String updId;

    private String updDate;

    private String etc;
}
