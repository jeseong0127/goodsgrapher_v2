package com.vitasoft.goodsgrapher.domain.model.kipris.entity;

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
@Table(name = "com_adjustment")
@Getter
@Setter
@DynamicUpdate
@RequiredArgsConstructor
public class Adjustment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int adjustNo;

    private int metaSeq;

    private String adjustId;

    private String regId;

    private String regName;

    private LocalDateTime regDate;

    private char userGrade;

    private char adjustYn;

    private int passCount;

    @Column(name = "SUBSCRIPTION")
    private String subScription;
}
