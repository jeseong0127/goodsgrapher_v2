package com.vitasoft.goodsgrapher.domain.exception.metadata;

public class ExceededReservedCountLimitException extends RuntimeException {
    public ExceededReservedCountLimitException() {
        super("최대 예약 개수는 3개입니다.");
    }
}
