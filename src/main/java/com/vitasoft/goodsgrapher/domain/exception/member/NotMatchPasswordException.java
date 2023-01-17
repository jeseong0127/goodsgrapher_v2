package com.vitasoft.goodsgrapher.domain.exception.member;

public class NotMatchPasswordException extends Exception {
    public NotMatchPasswordException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}
