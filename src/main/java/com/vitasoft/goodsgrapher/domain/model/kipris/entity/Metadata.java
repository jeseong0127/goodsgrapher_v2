package com.vitasoft.goodsgrapher.domain.model.kipris.entity;

import com.vitasoft.goodsgrapher.domain.model.sso.entity.Member;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "kps_metadata")
@Getter
@Setter
@DynamicUpdate
@RequiredArgsConstructor
public class Metadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int metaSeq;

    private String highCodeId;

    private String productCategory;

    private String codeId;

    private String articleName;

    private String modelName;

    private String companyName;

    private String registrationNumber;

    private String applicationNumber;

    @Column(name = "LASTRIGHT_HOLDERNAME")
    private String lastRightHolderName;

    private String pathImg;

    private String pathImgGoods;

    @Column(name = "RESERV_ID")
    private String reserveId;

    @Column(name = "RESERV_DATE")
    private LocalDateTime reserveDate;

    private String inspectorId;

    private String regId;

    private String regName;

    private LocalDateTime regDate;

    private int imgCount;

    private char useYn;

    @Column(name = "SUBSCRIPTION")
    private String subScription;

    @Column(name = "DSSHPCLSSCD")
    private String dsshpclsscd;

    public void startWork(Member member, String defaultReserveId, int defaultImageCount) {
        this.setReserveId(defaultReserveId);
        this.setReserveDate(null);
        this.setRegId(member.getMemberId());
        this.setRegName(member.getMemberName());
        this.setRegDate(LocalDateTime.now());
        this.setImgCount(defaultImageCount);
        this.setSubScription(member.getSubscription());
    }

    public void deleteMetadata() {
        regId = null;
        regName = null;
        regDate = null;
        this.setInspectorId(null);
        this.setSubScription(null);
        imgCount -= 62;
    }
}
