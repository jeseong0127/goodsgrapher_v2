package com.vitasoft.goodsgrapher.application.response;

import com.vitasoft.goodsgrapher.domain.model.dto.GetMetadataDetailDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MetadataDetailResponse {
    private final GetMetadataDetailDto metadata;
}
