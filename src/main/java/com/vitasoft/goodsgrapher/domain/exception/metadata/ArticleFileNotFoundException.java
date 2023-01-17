package com.vitasoft.goodsgrapher.domain.exception.metadata;

public class ArticleFileNotFoundException extends RuntimeException {
    public ArticleFileNotFoundException() {
        super("존재하지 않는 이미지입니다.");
    }
}
