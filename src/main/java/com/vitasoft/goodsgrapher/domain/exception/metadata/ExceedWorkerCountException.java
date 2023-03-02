package com.vitasoft.goodsgrapher.domain.exception.metadata;

public class ExceedWorkerCountException extends RuntimeException {
    public ExceedWorkerCountException() {
        super("최대 작업자수 3명을 초과했습니다.");
    }
}
