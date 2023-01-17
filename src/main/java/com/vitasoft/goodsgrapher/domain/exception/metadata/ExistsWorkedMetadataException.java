package com.vitasoft.goodsgrapher.domain.exception.metadata;

public class ExistsWorkedMetadataException extends RuntimeException {
    public ExistsWorkedMetadataException() {
        super("이미 작업한 메타데이터입니다.");
    }
}
