package com.vitasoft.goodsgrapher.domain.model.dto;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Work;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetAccountsDto {
    private int modelSeq;
    private LocalDateTime regDate;
    private String regId;
    private String status;
    private LocalDateTime updDate;
    private int workSeq;
    private LocalDateTime inspectDate;
    private String inspectorId;
    private int workedCount;

    public GetAccountsDto(Work work) {
        this.modelSeq = work.getModelSeq();
        this.regDate = work.getRegDate();
        this.regId = work.getRegId();
        this.status = work.getStatus();
        this.updDate = work.getUpdDate();
        this.workSeq = work.getWorkSeq();
        this.inspectDate = work.getInspectDate();
        this.inspectorId = work.getInspectorId();
    }

    public GetAccountsDto(Work work, int workedCount) {
        this(work);
        this.workedCount = workedCount;
    }
}
