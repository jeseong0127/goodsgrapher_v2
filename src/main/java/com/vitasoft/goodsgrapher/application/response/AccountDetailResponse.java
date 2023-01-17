package com.vitasoft.goodsgrapher.application.response;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ArticleFile;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccountDetailResponse {
    ModelInfo metadata;

    List<ArticleFile> articleFiles;
}
