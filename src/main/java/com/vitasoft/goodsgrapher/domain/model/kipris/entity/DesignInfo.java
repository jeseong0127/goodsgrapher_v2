package com.vitasoft.goodsgrapher.domain.model.kipris.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

    private String registrationNumber;

    private String regReferenceNumber;

    private String designNumber;

    private String applicationNumber;

    private String openDesignStatus;

    private String articleName;

    private String applicantName;

    private String applicantAddress;

    @Column(name = "LASTRIGHT_HOLDER_NAME")
    private String lastRightHolderName;

    @Column(name = "LASTRIGHT_HOLDER_ADDRESS")
    private String lastRightHolderAddress;

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

    private LocalDateTime updDate;

    private String etc;
}
