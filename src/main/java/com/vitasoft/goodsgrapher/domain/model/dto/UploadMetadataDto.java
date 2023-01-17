package com.vitasoft.goodsgrapher.domain.model.dto;

import com.sun.istack.NotNull;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Getter
public class UploadMetadataDto {
    @NotNull
    private final int metaSeq;

    @NotNull
    private final List<MultipartFile> images;
}
