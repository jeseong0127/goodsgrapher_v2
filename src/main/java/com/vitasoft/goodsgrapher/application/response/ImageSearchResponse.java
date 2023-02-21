package com.vitasoft.goodsgrapher.application.response;

import com.vitasoft.goodsgrapher.domain.model.dto.GetImageSearchDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ImageSearchResponse {
    private final GetImageSearchDto getImageSearch;
}
