package com.vitasoft.goodsgrapher.domain.service;

import com.vitasoft.goodsgrapher.application.request.DeleteMetadataRequest;
import com.vitasoft.goodsgrapher.application.request.MetadataRequest;
import com.vitasoft.goodsgrapher.domain.exception.metadata.DuplicationReserveIdException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.ExceededReservedCountLimitException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.ModelInfoNotFoundException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.WorkModelNotFoundException;
import com.vitasoft.goodsgrapher.domain.model.dto.GetCategoryDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetMetadataDetailDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetMetadataDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetModelImageDto;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelImage;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Work;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.CodeRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.DesignInfoRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ModelImageRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ModelInfoRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.WorkRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MetadataService {

    private final ImageService imageService;

    private final ModelInfoRepository modelInfoRepository;

    private final ModelImageRepository modelImageRepository;

    private final DesignInfoRepository designInfoRepository;

    private final WorkRepository workRepository;

    private final CodeRepository codeRepository;

    public List<GetMetadataDto> getMetadataList() {
        return modelInfoRepository.findAll().stream().map(GetMetadataDto::new).collect(Collectors.toList());
    }

    public List<GetMetadataDto> getSearchMetadata(String searchWord, String codeId) {
        return modelInfoRepository.findAllByMetadata(codeId, searchWord == null ? "" : searchWord, searchWord, searchWord).stream().map(GetMetadataDto::new).collect(Collectors.toList());
    }

    public List<GetMetadataDto> getImageSearchMetadata(List<String> images) {
        List<GetMetadataDto> metadataDtos = new ArrayList<>();

        List<ModelInfo> modelInfoList = modelInfoRepository.findAllByModelNameIsNotNullAndUseYn('Y');

        cancelExcessReserveTime();

        for (String pathImage : images) {
            modelInfoList.forEach(modelInfo -> {
                if (modelInfo.getPathImgGoods() != null && modelInfo.getPathImgGoods().contains(pathImage)) {
                    metadataDtos.add(new GetMetadataDto(modelInfo, "photo"));
                } else {
                    if (modelInfo.getDesignInfo().getImgPath() != null && modelInfo.getDesignInfo().getImgPath().contains(pathImage))
                        metadataDtos.add(new GetMetadataDto(modelInfo, "drawing", modelInfo.getDesignInfo().getImgPath()));
                }
            });
        }

        return metadataDtos;
    }

    public void cancelExcessReserveTime() {
        LocalDateTime now = LocalDateTime.now();

        workRepository.findAllByStatus("1").stream().filter(work -> work.getRegDate().plusDays(2).isBefore(now)).forEach(work -> cancelReserveMetadata(work.getWorkSeq(), work.getRegId()));
    }

    public void reserveMetadata(String memberId, int modelSeq) {
        int maxReserveCount = 3;
        if (workRepository.countByRegIdAndStatus(memberId, "1") >= maxReserveCount)
            throw new ExceededReservedCountLimitException();

        Work worked = workRepository.findTopByModelSeqAndRegIdOrderByRegDateDesc(modelSeq, memberId);

        if (worked != null && !Objects.equals(worked.getStatus(), "0"))
            throw new DuplicationReserveIdException(modelSeq);

        Work work = new Work(memberId, modelSeq);
        work.reserve();
        workRepository.save(work);
    }

    public void cancelReserveMetadata(int modelSeq, String memberId) {
        Work work = workRepository.findTopByModelSeqAndRegIdOrderByRegDate(modelSeq, memberId);
        List<ModelImage> modelImage = modelImageRepository.findAllByModelSeqAndRegId(modelSeq, memberId);
        modelImage.forEach(image -> image.setIsDeleted("1"));
        work.setStatus("0");
    }

    @Transactional(readOnly = true)
    public GetMetadataDetailDto getMetadataDetail(int modelSeq) {
        ModelInfo modelInfo = modelInfoRepository.findById(modelSeq).orElseThrow(() -> new ModelInfoNotFoundException(modelSeq));

        DesignInfo designInfo = designInfoRepository.findByRegistrationNumber(modelInfo.getRegistrationNumber());

        return new GetMetadataDetailDto(modelInfo, designInfo);
    }

    @Transactional(readOnly = true)
    public List<GetModelImageDto> getMetadataImages(int modelSeq, String memberId) {
        return modelImageRepository.findByModelSeqAndIsDeletedAndRegId(modelSeq, "0", memberId).stream().map(GetModelImageDto::new).collect(Collectors.toList());
    }

    public void uploadMetadata(String memberId, int modelSeq, List<JSONObject> jsonObjectList, List<MultipartFile> images) {
        int displayOrder = modelImageRepository.findTopByModelSeqAndRegIdOrderByUploadSeqDesc(modelSeq, memberId).orElseGet(ModelImage::new).getDisplayOrder();

        ModelInfo modelInfo = modelInfoRepository.findById(modelSeq).orElseThrow(() -> new ModelInfoNotFoundException(modelSeq));

        DesignInfo designInfo = designInfoRepository.findByRegistrationNumber(modelInfo.getRegistrationNumber());

        Work work = new Work(memberId, modelSeq);
        work.start();
        workRepository.save(work);
        uploadMetadataImages(memberId, modelInfo, displayOrder, designInfo, jsonObjectList, images);

    }

    private void uploadMetadataImages(String memberId, ModelInfo modelInfo, int displayOrder, DesignInfo designInfo, List<JSONObject> jsonObjectList, List<MultipartFile> images) {
        List<ModelImage> imgArray = new ArrayList<>();
        Map<Integer, List<MultipartFile>> imageGroups = getImageGroups(images);
        imageGroups.forEach((key, subImages) -> {
            for (int i = 0; i < subImages.size(); i++) {
                ModelImage modelImage = imageService.uploadMetadataImage(memberId, modelInfo, subImages.get(i), i + 1, designInfo, jsonObjectList.get((key * 10) + i));
                imgArray.add(modelImage);
            }
        });
        modelImageRepository.saveAll(imgArray);
    }

    private Map<Integer, List<MultipartFile>> getImageGroups(List<MultipartFile> images) {
        Map<Integer, List<MultipartFile>> groups = new TreeMap<>();

        images.forEach(image -> {
            int lastIndex = Objects.requireNonNull(image.getOriginalFilename()).lastIndexOf("_");
            int indexNumber = Integer.parseInt(image.getOriginalFilename().substring(lastIndex + 1, lastIndex + 2));

            groups.putIfAbsent(indexNumber, new ArrayList<>());
            List<MultipartFile> subImages = groups.get(indexNumber);
            subImages.add(image);
        });

        return groups;
    }

//    public void updateMetadata(String memberId, MetadataRequest metadataRequest) {
//
//        ModelInfo metadata = metadataRepository.findById(metadataRequest.getMetaSeq()).orElseThrow(() -> new MetadataNotFoundException(metadataRequest.getMetaSeq()));
//
//        int maxDisplayOrder = articleFileRepository.findTopByArticleIdOrderByArticleFileIdDesc(metadataRequest.getMetaSeq()).getDisplayOrder();
//
//        for (int i = 0; i < metadataRequest.getImages().size(); i++) {
//            ArticleFile articleFile = imageService.uploadMetadataImage(memberId, metadata, metadataRequest.getImages().get(i), maxDisplayOrder + (i + 1));
//            articleFileRepository.save(articleFile);
//        }
//    }

    public void deleteMetadata(DeleteMetadataRequest deleteMetadataRequest) throws IOException {
        for (int uploadSeq : deleteMetadataRequest.getUploadSeqList()) {
            ModelImage modelImage = modelImageRepository.findById(uploadSeq).orElseThrow(ArithmeticException::new);
            modelImage.setIsDeleted("1");
            imageService.deleteMetadataImage(modelImage.getFileName());
        }
    }

    public void deleteWorkedMetadata(int modelSeq, String memberId) {
        ModelInfo modelInfo = modelInfoRepository.findById(modelSeq).orElseThrow(() -> new ModelInfoNotFoundException(modelSeq));

        List<ModelImage> modelImages = modelImageRepository.findAllByModelSeqAndRegId(modelInfo.getModelSeq(), memberId);
        modelImageRepository.deleteAll(modelImages);

//        Work work = new Work(memberId, modelSeq);
//        work.cancel();
//        workRepository.save(work);
    }

    @Transactional(readOnly = true)
    public List<GetCategoryDto> getHighCategory() {
        return codeRepository.findAllByHighCodeIdAndCodeGroup("TOPCODE", "GOODS").stream().map(GetCategoryDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GetCategoryDto> getDownCategory(String codeId) {
        return codeRepository.findAllByHighCodeId(codeId).stream().map(GetCategoryDto::new).collect(Collectors.toList());
    }

    public JSONObject convertJson(String json) {
        JSONParser parser = new JSONParser();
        Object obj;
        try {
            obj = parser.parse(json);
            return (JSONObject) obj;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
