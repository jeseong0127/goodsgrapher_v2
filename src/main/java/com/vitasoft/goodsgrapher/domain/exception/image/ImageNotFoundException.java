package com.vitasoft.goodsgrapher.domain.exception.image;

public class ImageNotFoundException extends RuntimeException {
    public ImageNotFoundException(int imageId) {
        super(imageId + "번 이미지가 존재하지 않습니다.");
    }
}
