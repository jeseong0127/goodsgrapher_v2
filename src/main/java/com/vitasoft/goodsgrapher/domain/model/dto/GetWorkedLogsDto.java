package com.vitasoft.goodsgrapher.domain.model.dto;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Work;

import lombok.Data;

@Data
public class GetWorkedLogsDto {
    private Work work;
    private ModelInfo modelInfo;

    public GetWorkedLogsDto(Work work, ModelInfo modelInfo) {
        this.work = work;
        this.modelInfo = modelInfo;
    }
}
