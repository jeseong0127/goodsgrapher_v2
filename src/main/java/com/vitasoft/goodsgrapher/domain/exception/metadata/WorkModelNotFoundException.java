package com.vitasoft.goodsgrapher.domain.exception.metadata;

public class WorkModelNotFoundException extends RuntimeException {
    public WorkModelNotFoundException(int workSeq) {
        super("작업한 정보를 찾을 수 없습니다.\n"
                + "workSeq: " + workSeq);
    }
}
