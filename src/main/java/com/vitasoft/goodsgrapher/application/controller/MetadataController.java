package com.vitasoft.goodsgrapher.application.controller;

import com.vitasoft.goodsgrapher.application.request.DeleteMetadataRequest;
import com.vitasoft.goodsgrapher.application.request.MetadataRequest;
import com.vitasoft.goodsgrapher.application.response.CategoryResponse;
import com.vitasoft.goodsgrapher.application.response.MetadataDetailResponse;
import com.vitasoft.goodsgrapher.application.response.MetadataResponse;
import com.vitasoft.goodsgrapher.application.response.ModelImageResponse;
import com.vitasoft.goodsgrapher.core.security.AuthenticatedMember;
import com.vitasoft.goodsgrapher.core.security.MemberInfo;
import com.vitasoft.goodsgrapher.domain.service.MetadataService;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/metadata")
@RequiredArgsConstructor
public class MetadataController {
    private final MetadataService metadataService;

    @ApiOperation("메타데이터 조회하기")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public MetadataResponse getMetadata(
            @RequestParam(required = false) String word,
            @RequestParam(required = false) List<String> images,
            @RequestParam(required = false) String codeId
    ) {
        if (word != null || codeId != null) {
            return new MetadataResponse(metadataService.getSearchMetadata(word, codeId));
        } else if (images != null) {
            return new MetadataResponse(metadataService.getImageSearchMetadata(images));
        } else {
            return new MetadataResponse(metadataService.getMetadataList());
        }
    }

    @ApiOperation("메타데이터 예약하기")
    @PostMapping("/reservation/{modelSeq}")
    @ResponseStatus(HttpStatus.CREATED)
    public void reserveMetadata(
            @MemberInfo AuthenticatedMember member,
            @PathVariable int modelSeq
    ) {
        metadataService.reserveMetadata(member.getMemberId(), modelSeq);
    }

    @ApiOperation("메타데이터 예약 취소하기")
    @DeleteMapping("/reservation/{modelSeq}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelReserveMetadata(
            @MemberInfo AuthenticatedMember member,
            @PathVariable int modelSeq
    ) {
        metadataService.cancelReserveMetadata(modelSeq, member.getMemberId());
    }

    @ApiOperation("메타데이터 작업보기")
    @GetMapping("/{modelSeq}")
    @ResponseStatus(HttpStatus.OK)
    public MetadataDetailResponse getMetadataDetail(
            @PathVariable int modelSeq
    ) {
        return new MetadataDetailResponse(metadataService.getMetadataDetail(modelSeq));
    }

    @ApiOperation("메타데이터 작업 삭제하기")
    @DeleteMapping("/{modelSeq}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteWorkedMetadata(
            @MemberInfo AuthenticatedMember member,
            @PathVariable int modelSeq
    ) {
        metadataService.deleteWorkedMetadata(modelSeq, member.getMemberId());
    }

    @ApiOperation("작업한 메타데이터 이미지보기")
    @GetMapping("/{modelSeq}/images")
    @ResponseStatus(HttpStatus.OK)
    public ModelImageResponse getMetadataImages(
            @MemberInfo AuthenticatedMember member,
            @PathVariable int modelSeq
    ) {
        return new ModelImageResponse(metadataService.getMetadataImages(modelSeq, member.getMemberId()));
    }

    @ApiOperation("메타데이터 작업하기")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE, "application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadMetadata(
            @MemberInfo AuthenticatedMember member,
            @Valid @ModelAttribute MetadataRequest metadataRequest,
            @RequestPart List<MultipartFile> images
    ) {
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (int i = 0; i < metadataRequest.getJson().size(); i++) {
            jsonObjectList.add(metadataService.convertJson(metadataRequest.getJson().get(i)));
        }
        metadataService.deleteWorkedMetadata(metadataRequest.getModelSeq(), member.getMemberId());
        metadataService.uploadMetadata(member.getMemberId(), metadataRequest.getModelSeq(), jsonObjectList , images);
    }

//    @ApiOperation("메타데이터 수정하기")
//    @PutMapping
//    @ResponseStatus(HttpStatus.OK)
//    public void updateMetadata(
//            @MemberInfo AuthenticatedMember member,
//            @Valid @ModelAttribute MetadataRequest metadataRequest
//    ) {
//        metadataService.updateMetadata(member.getMemberId(), metadataRequest);
//    }

    @ApiOperation("메타데이터 삭제하기")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMetadata(
            @Valid @ModelAttribute DeleteMetadataRequest deleteMetadataRequest
    ) throws IOException {
        metadataService.deleteMetadata(deleteMetadataRequest);
    }

    @ApiOperation("상위 카테고리 가져오기")
    @GetMapping("/high-category")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponse getHighCategory(
    ) {
        return new CategoryResponse(metadataService.getHighCategory());
    }

    @ApiOperation("하위 카테고리 가져오기")
    @GetMapping("/high-category/{codeId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponse getDownCategory(
            @PathVariable String codeId
    ) {
        return new CategoryResponse(metadataService.getDownCategory(codeId));
    }
}
