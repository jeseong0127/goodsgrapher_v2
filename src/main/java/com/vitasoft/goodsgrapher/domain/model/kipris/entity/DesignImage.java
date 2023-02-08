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
@Table(name = "meta_design_images")
@Data
@DynamicUpdate
@RequiredArgsConstructor
public class DesignImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int designImgSeq;

    private String imageName;

    private String imgUrl;

    private String imgPath;

    private int imgNumber;

    private String updId;

    private LocalDateTime updDate;

    public DesignImage(int designImgSeq, String imageName, String imgUrl, String imgPath, int imgNumber, String updId, LocalDateTime updDate) {
        this.designImgSeq = designImgSeq;
        this.imageName = imageName;
        this.imgUrl = imgUrl;
        this.imgPath = imgPath;
        this.imgNumber = imgNumber;
        this.updId = updId;
        this.updDate = updDate;
    }
}
