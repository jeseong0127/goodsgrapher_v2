package com.vitasoft.goodsgrapher.application.controller;

import com.vitasoft.goodsgrapher.domain.service.ImageService;
import io.swagger.annotations.ApiOperation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {
    private final ImageService imageService;

    @ApiOperation(value = "이미지 조회하기")
    @GetMapping(value = "/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] viewImage(
            @PathVariable int imageId
    ) {
        return imageService.viewImage(imageId);
    }

    @ApiOperation(value = "검색 대표 이미지 조회하기")
    @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] viewThumbnailImage(
            @RequestParam String imagePath
    ) {
        return imageService.viewThumbnailImage(imagePath);
    }

    @ApiOperation(value = "검수 상세 이미지 조회하기")
    @GetMapping(value = "/inspect/{imageId}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] viewInspectImage(
            @PathVariable int imageId
    ) {
        return imageService.viewInspectImage(imageId);
    }
}
