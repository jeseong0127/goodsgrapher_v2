package com.vitasoft.goodsgrapher.domain.exception.image;

public class CannotUploadPdfException extends RuntimeException {
    public CannotUploadPdfException() {
        super("pdf 첨부 과정에서 문제가 발생하였습니다.");
    }
}
