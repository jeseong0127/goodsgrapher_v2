package com.vitasoft.goodsgrapher.domain.model.kipris.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "meta_design_info")
@Data
@DynamicUpdate
@RequiredArgsConstructor
public class DesignInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int designSeq;

    @OneToMany
    @JoinColumn(name = "designSeq")
    private List<DesignImages> designImages = new ArrayList<>();

    private String registrationNumber;

    private String designNumber;

    private String applicationNumber;

    private String openDesignStatus;

    private String articleName;

    private String applicantName;

    @Column(name = "LASTRIGHT_HOLDERNAME")
    private String lastRightHolderName;

    private String imgUrl;

    private String imgPath;

    private String pdfUrl;

    private String pdfPath;

    private String classCode;

    private String agentName;

    private String agentAddress;

    private String registrationDate;

    private String classCodeInt;

    private String updId;

    private String updDate;

    private String etc;
}
