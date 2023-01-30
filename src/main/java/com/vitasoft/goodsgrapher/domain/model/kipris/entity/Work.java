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
@Table(name = "meta_model_work")
@Data
@DynamicUpdate
@RequiredArgsConstructor
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int workSeq;

    private int modelSeq;

    private String regId;

    private LocalDateTime regDate;

    private String status;

    public Work(String regId, int modelSeq) {
        this.modelSeq = modelSeq;
        this.regId = regId;
        this.regDate = LocalDateTime.now();
    }

    public void reserve() {
        this.status = "1";
    }

    public void start() {
        this.status = "2";
    }

    public void cancel() {
        this.status = "0";
    }
}
