package com.vitasoft.goodsgrapher.domain.exception.metadata;

public class MetadataNotFoundException extends RuntimeException {
    public MetadataNotFoundException(int modelSeq) {
        super("metadata 가 존재하지 않습니다. \n "
                + "modelSeq: " + modelSeq);
    }
}
