package com.vitasoft.goodsgrapher.domain.model.dto;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignInfo;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetDesignInfoDto {
    private final String agentAddress;
    private final String agentName;
    private final String applicantAddress;
    private final String applicantName;
    private final String applicationNumber;
    private final String articleName;
    private final String classCode;
    private final String classCodeInt;
    private final String designNumber;
    private final int designSeq;
    private final String etc;
    private final String imgPath;
    private final String imgUrl;
    private final String lastRightHolderAddress;
    private final String lastRightHolderName;
    private final String openDesignStatus;
    private final String regReferenceNumber;
    private final String registrationDate;
    private final String registrationNumber;
    private final LocalDateTime updDate;
    private final String updId;
    private List<GetDesignImageDto> designImages;

    public GetDesignInfoDto(DesignInfo designInfo, List<GetDesignImageDto> designImages) {
        this.agentAddress = designInfo.getAgentAddress();
        this.agentName = designInfo.getAgentName();
        this.applicantAddress = designInfo.getApplicantAddress();
        this.applicantName = designInfo.getApplicantName();
        this.applicationNumber = designInfo.getApplicationNumber();
        this.articleName = designInfo.getArticleName();
        this.classCode = designInfo.getClassCode();
        this.classCodeInt = designInfo.getClassCodeInt();
        this.designNumber = designInfo.getDesignNumber();
        this.designSeq = designInfo.getDesignSeq();
        this.etc = designInfo.getEtc();
        this.imgPath = designInfo.getImgPath();
        this.imgUrl = designInfo.getImgUrl();
        this.lastRightHolderAddress = designInfo.getLastRightHolderAddress();
        this.lastRightHolderName = designInfo.getLastRightHolderName();
        this.openDesignStatus = designInfo.getOpenDesignStatus();
        this.regReferenceNumber = designInfo.getRegReferenceNumber();
        this.registrationDate = designInfo.getRegistrationDate();
        this.registrationNumber = designInfo.getRegistrationNumber();
        this.updDate = designInfo.getUpdDate();
        this.updId = designInfo.getUpdId();
        this.designImages = designImages;
    }
}
