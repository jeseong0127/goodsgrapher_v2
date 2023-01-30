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
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelImages;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Work;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.CodeRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.DesignInfoRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ModelImagesRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ModelInfoRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.WorkRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class MetadataService {

    private final ImageService imageService;

    private final ModelInfoRepository modelInfoRepository;

    private final ModelImagesRepository modelImagesRepository;

    private final DesignInfoRepository designInfoRepository;

    private final WorkRepository workRepository;

    private final CodeRepository codeRepository;

    public List<GetMetadataDto> getMetadataList() {
        return modelInfoRepository.findAll().stream()
                .map(GetMetadataDto::new)
                .collect(Collectors.toList());
    }

    public List<GetMetadataDto> getSearchMetadata(String searchWord, String codeId) {
        return modelInfoRepository.findAllByMetadata(codeId, searchWord == null ? "" : searchWord, searchWord, searchWord).stream()
                .map(GetMetadataDto::new)
                .collect(Collectors.toList());
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

        workRepository.findAllByStatus("1").stream()
                .filter(work -> work.getRegDate().plusDays(2).isBefore(now))
                .forEach(work -> cancelReserveMetadata(work.getWorkSeq()));
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

    public void cancelReserveMetadata(int workSeq) {
        Work work = workRepository.findById(workSeq)
                .orElseThrow(() -> new WorkModelNotFoundException(workSeq));

        work.setStatus("0");
    }

    @Transactional(readOnly = true)
    public GetMetadataDetailDto getMetadataDetail(int modelSeq) {
        ModelInfo modelInfo = modelInfoRepository.findById(modelSeq)
                .orElseThrow(() -> new ModelInfoNotFoundException(modelSeq));

        DesignInfo designInfo = designInfoRepository.findByRegistrationNumber(modelInfo.getRegistrationNumber());

        return new GetMetadataDetailDto(modelInfo, designInfo);
    }

    @Transactional(readOnly = true)
    public List<GetModelImageDto> getMetadataImages(int modelSeq, String memberId) {
        return modelImagesRepository.findByModelSeqAndIsDeletedAndRegId(modelSeq, "0", memberId).stream()
                .map(GetModelImageDto::new)
                .collect(Collectors.toList());
    }

    public void uploadMetadata(String memberId, MetadataRequest metadataRequest) {
        int displayOrder = modelImagesRepository.findTopByModelSeqAndRegIdOrderByUploadSeqDesc(metadataRequest.getModelSeq(), memberId)
                .orElseGet(ModelImages::new).getDisplayOrder();

        ModelInfo modelInfo = modelInfoRepository.findById(metadataRequest.getModelSeq()).orElseThrow(() -> new ModelInfoNotFoundException(metadataRequest.getModelSeq()));

        DesignInfo designInfo = designInfoRepository.findByRegistrationNumber(modelInfo.getRegistrationNumber());

        Work work = new Work(memberId, metadataRequest.getModelSeq());
        work.start();
        workRepository.save(work);

        uploadMetadataImages(memberId, metadataRequest, modelInfo, displayOrder, designInfo);

    }

    private void uploadMetadataImages(String memberId, MetadataRequest metadataRequest, ModelInfo modelInfo, int displayOrder, DesignInfo designInfo) {
        if (metadataRequest.getImages() != null) {
            for (MultipartFile image : metadataRequest.getImages()) {
                ModelImages modelImages = imageService.uploadMetadataImage(memberId, modelInfo, image, ++displayOrder, designInfo);
                modelImagesRepository.save(modelImages);
            }
        }
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
            ModelImages modelImages = modelImagesRepository.findById(uploadSeq)
                    .orElseThrow(ArithmeticException::new);
            modelImages.setIsDeleted("1");
            imageService.deleteMetadataImage(modelImages.getFileName());
        }
    }

    public void deleteWorkedMetadata(int modelSeq, String memberId) {
        ModelInfo modelInfo = modelInfoRepository.findById(modelSeq).orElseThrow(() -> new ModelInfoNotFoundException(modelSeq));

        List<ModelImages> modelImages = modelImagesRepository.findAllByModelSeqAndRegId(modelInfo.getModelSeq(), memberId);
        modelImages.forEach(images -> images.setIsDeleted("1"));

        Work work = new Work(memberId, modelSeq);
        work.cancel();
        workRepository.save(work);
    }

    @Transactional(readOnly = true)
    public List<GetCategoryDto> getHighCategory() {
        return codeRepository.findAllByHighCodeIdAndCodeGroup("TOPCODE", "GOODS").stream()
                .map(GetCategoryDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GetCategoryDto> getDownCategory(String codeId) {
        return codeRepository.findAllByHighCodeId(codeId).stream()
                .map(GetCategoryDto::new)
                .collect(Collectors.toList());
    }
}
