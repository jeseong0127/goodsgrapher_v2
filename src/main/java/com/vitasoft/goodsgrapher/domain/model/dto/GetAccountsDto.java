package com.vitasoft.goodsgrapher.domain.model.dto;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Adjustment;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class GetAccountsDto {
    private final int adjustNo;
    private final int metaSeq;
    private final String adjustId;
    private final String regId;
    private final String regName;
    private final LocalDateTime regDate;
    private final char userGrade;
    private final char adjustYn;
    private final int passCount;
    private final String subScription;

    public GetAccountsDto(Adjustment adjustment) {
        this.adjustNo = adjustment.getAdjustNo();
        this.metaSeq = adjustment.getMetaSeq();
        this.adjustId = adjustment.getAdjustId();
        this.regId = adjustment.getRegId();
        this.regName = adjustment.getRegName();
        this.regDate = adjustment.getRegDate();
        this.userGrade = adjustment.getUserGrade();
        this.adjustYn = adjustment.getAdjustYn();
        this.passCount = adjustment.getPassCount();
        this.subScription = adjustment.getSubScription();
    }
}
