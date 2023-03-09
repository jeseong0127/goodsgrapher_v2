package com.vitasoft.goodsgrapher.application.response;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignImage;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelImage;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccountDetailResponse {
    List<DesignImage> designImages;

    List<ModelImage> ModelImages;
}
