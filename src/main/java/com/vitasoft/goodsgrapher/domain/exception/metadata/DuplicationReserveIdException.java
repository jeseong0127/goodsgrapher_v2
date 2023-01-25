package com.vitasoft.goodsgrapher.domain.exception.metadata;

public class DuplicationReserveIdException extends RuntimeException {
    public DuplicationReserveIdException(int modelSeq) {
        super("이미 예약한 작업입니다. \n" +
                "metaSeq: " + modelSeq);
    }
}
