package com.vitasoft.goodsgrapher.application.request;

import com.sun.istack.NotNull;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DeleteMetadataRequest {
    @NotNull
    private final int metaSeq;

    @NotNull
    private final List<Integer> articleFileId;
}
