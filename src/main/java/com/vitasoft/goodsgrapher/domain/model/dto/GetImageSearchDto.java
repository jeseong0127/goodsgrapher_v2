package com.vitasoft.goodsgrapher.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetImageSearchDto {
    private final String imgPath;

    private final String registrationNumber;

    private final String lastRightHolderName;
}

//public interface GetImageSearchProjection {
//    String getImgPath();
//
//    String getRregistrationNumber;
//
//    String lastRightHolderName;
//}
