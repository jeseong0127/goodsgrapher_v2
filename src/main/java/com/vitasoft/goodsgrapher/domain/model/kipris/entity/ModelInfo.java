package com.vitasoft.goodsgrapher.domain.model.kipris.entity;

import java.time.LocalDateTime;
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
@Table(name = "meta_model_info")
@Data
@DynamicUpdate
@RequiredArgsConstructor
public class ModelInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int modelSeq;

    @OneToOne
    @JoinColumn(name = "modelSeq")
    private DesignInfo designInfo;

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

//    public void startWork(Member member, String defaultReserveId, int defaultImageCount) {
//        this.reserveId = defaultReserveId;
//        this.reserveDate = null;
//        this.regId = member.getMemberId();
//        this.regName = member.getMemberName();
//        this.regDate = LocalDateTime.now();
//        this.imgCount = defaultImageCount;
//        this.subScription = member.getSubscription();
//    }
//
//    public void deleteMetadata() {
//        this.regId = null;
//        this.regName = null;
//        this.regDate = null;
//        this.inspectorId = null;
//        this.subScription = null;
//        this.imgCount -= 62;
//    }
}
