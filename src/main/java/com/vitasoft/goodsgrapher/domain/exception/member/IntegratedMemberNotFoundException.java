package com.vitasoft.goodsgrapher.domain.exception.member;

public class IntegratedMemberNotFoundException extends RuntimeException {
    public IntegratedMemberNotFoundException() {
        super("통합회원이 아닙니다.");
    }
}
