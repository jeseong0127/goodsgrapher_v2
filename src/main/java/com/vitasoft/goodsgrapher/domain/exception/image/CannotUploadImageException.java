package com.vitasoft.goodsgrapher.domain.exception.image;

public class CannotUploadImageException extends RuntimeException {
    public CannotUploadImageException() {
        super("이미지 첨부 과정에서 문제가 발생했습니다.");
    }
}
