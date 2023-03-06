package com.vitasoft.goodsgrapher.application.request;

import com.sun.istack.NotNull;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MetadataRequest {
    @NotNull
    private int modelSeq;

    @NotNull
    private List<String> json;
}
