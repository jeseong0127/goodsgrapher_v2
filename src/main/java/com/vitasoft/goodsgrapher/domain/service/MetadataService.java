package com.vitasoft.goodsgrapher.domain.service;

import com.vitasoft.goodsgrapher.application.request.MetadataRequest;
import com.vitasoft.goodsgrapher.domain.exception.metadata.DuplicationReserveIdException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.ExceededReservedCountLimitException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.ModelInfoNotFoundException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.WorkModelNotFoundException;
import com.vitasoft.goodsgrapher.domain.model.dto.GetCategoryDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetMetadataDetailDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetMetadataDto;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelImages;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Work;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ArticleFileRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.CodeRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.DesignImagesRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.DesignInfoRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ModelImagesRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ModelInfoRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.WorkRepository;
import com.vitasoft.goodsgrapher.domain.model.sso.repository.MemberRepository;

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

    private final ArticleFileRepository articleFileRepository;

    private final ModelInfoRepository modelInfoRepository;

    private final ModelImagesRepository modelImagesRepository;

    private final DesignInfoRepository designInfoRepository;

    private final WorkRepository workRepository;

    private final CodeRepository codeRepository;

    private final MemberRepository memberRepository;

    private final DesignImagesRepository designImagesRepository;

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
                    DesignInfo designInfo = designInfoRepository.findByRegistrationNumber(modelInfo.getRegistrationNumber());
                    if (designInfo.getImgPath() != null && designInfo.getImgPath().contains(pathImage))
                        metadataDtos.add(new GetMetadataDto(modelInfo, "drawing", designInfo.getImgPath()));
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

        Work work = workRepository.findTopByModelSeqAndRegIdOrderByRegDateDesc(modelSeq, memberId);

        if (work != null && !Objects.equals(work.getStatus(), "0"))
            throw new DuplicationReserveIdException(modelSeq);

        workRepository.save(new Work().reserve(memberId, modelSeq));
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

//    @Transactional(readOnly = true)
//    public List<GetArticleFileDto> getMetadataImages(int metaSeq, String memberId) {
//        return articleFileRepository.findAllByBoardNameAndArticleIdAndIsDeletedAndRegId("METAIMG", metaSeq, "0", memberId).stream()
//                .map(GetArticleFileDto::new)
//                .collect(Collectors.toList());
//    }

    public void uploadMetadata(String memberId, MetadataRequest metadataRequest) {
        int displayOrder = 0;

        ModelInfo modelInfo = modelInfoRepository.findById(metadataRequest.getModelSeq()).orElseThrow(() -> new ModelInfoNotFoundException(metadataRequest.getModelSeq()));

        DesignInfo designInfo = designInfoRepository.findByRegistrationNumber(modelInfo.getRegistrationNumber());

        workRepository.save(new Work().start(memberId, metadataRequest.getModelSeq()));

        uploadMetadataImages(memberId, metadataRequest, modelInfo, displayOrder, designInfo);

    }

    private void uploadMetadataImages(String memberId, MetadataRequest metadataRequest, ModelInfo modelInfo, int displayOrder, DesignInfo designInfo) {
        if (metadataRequest.getImages() != null) {
            for (MultipartFile image : metadataRequest.getImages()) {
                ModelImages modelImages = imageService.uploadMetadataImage(memberId, modelInfo, image, displayOrder++, designInfo);
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

//    public void deleteMetadata(String memberId, DeleteMetadataRequest deleteMetadataRequest) throws IOException {
//        modelRepository.findByModelSeqAndRegId(deleteMetadataRequest.getMetaSeq(), memberId)
//                .orElseThrow(RegIdIsNotWorkerException::new);
//
//        for (int articleFileId : deleteMetadataRequest.getArticleFileId()) {
//            ArticleFile articleFile = articleFileRepository.findById(articleFileId)
//                    .orElseThrow(ArticleFileNotFoundException::new);
//            articleFile.setIsDeleted("1");
//            articleFileRepository.save(articleFile);
//            imageService.deleteMetadataImage(articleFile.getFileName());
//        }
//    }

//    public void deleteWorkedMetadata(int metaSeq, String memberId) throws IOException {
//        ModelInfo metadata = metadataRepository.findById(metaSeq).orElseThrow(() -> new MetadataNotFoundException(metaSeq));
//
//        List<ArticleFile> articleFiles = articleFileRepository.findByArticleIdAndRegIdAndIsDeletedAndEtc2IsNull(metaSeq, memberId, "0");
//        articleFiles.forEach(articleFile -> articleFile.setIsDeleted("1"));
//        articleFileRepository.saveAll(articleFiles);
//
//        metadata.deleteMetadata();
//
//        for (ArticleFile articleFile : articleFiles) {
//            imageService.deleteMetadataImage(articleFile.getFileName());
//        }
//    }

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
