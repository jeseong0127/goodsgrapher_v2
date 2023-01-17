package com.vitasoft.goodsgrapher.domain.exception.image;

import java.io.File;

public class CannotViewImageException extends RuntimeException {
    public CannotViewImageException(File image) {
        super("이미지를 조회하는데 문제가 발생했습니다.\n" +
                "fileName: " + image.getAbsolutePath());
    }
}
