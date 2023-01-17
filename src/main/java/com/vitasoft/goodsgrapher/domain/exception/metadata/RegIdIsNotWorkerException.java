package com.vitasoft.goodsgrapher.domain.exception.metadata;

public class RegIdIsNotWorkerException extends RuntimeException {
    public RegIdIsNotWorkerException() {
        super("해당하는 메타데이터 작업자가 아닙니다.");
    }
}

