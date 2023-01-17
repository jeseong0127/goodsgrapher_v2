package com.vitasoft.goodsgrapher.domain.exception.member;

public class MemberNotUseException extends RuntimeException {
    public MemberNotUseException() {
        super("사용할 수 없는 회원입니다.");
    }
}
