package com.vitasoft.goodsgrapher.domain.exception.image;

public class CannotDeleteImageException extends RuntimeException {
    public CannotDeleteImageException() {
        super("이미지 삭제 과정에서 문제가 발생하였습니다.");
    }
}
