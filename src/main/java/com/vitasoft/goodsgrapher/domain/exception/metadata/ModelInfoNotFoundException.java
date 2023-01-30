package com.vitasoft.goodsgrapher.domain.exception.metadata;

public class ModelInfoNotFoundException extends RuntimeException {
    public ModelInfoNotFoundException(int modelSeq) {
        super("modelInfo 가 존재하지 않습니다. \n "
                + "modelSeq: " + modelSeq);
    }
}
