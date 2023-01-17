package com.vitasoft.goodsgrapher.domain.service;

import com.vitasoft.goodsgrapher.domain.model.dto.GetCategoryDto;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ArticleFileRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.CodeRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.DesignImagesRepository;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MetadataService {

    private final ImageService imageService;

    private final ArticleFileRepository articleFileRepository;

//    private final ModelRepository modelRepository;

    private final CodeRepository codeRepository;

    private final DesignImagesRepository designImagesRepository;

//    public List<GetMetadataDto> getMetadataList() {
//        return modelRepository.findAll().stream()
//                .map(GetMetadataDto::new)
//                .collect(Collectors.toList());
//    }

//    public List<GetMetadataDto> getSearchMetadata(String searchWord, String codeId) {
//        return modelRepository.findAllByMetadata(codeId, searchWord == null ? "" : searchWord, searchWord, searchWord).stream()
//                .map(GetMetadataDto::new)
//                .collect(Collectors.toList());
//    }

//    public List<GetMetadataDto> getImageSearchMetadata(List<String> images) {
//        List<GetMetadataDto> metadataDtos = new ArrayList<>();
//
//        List<ModelInfo> metadataList = modelRepository.findAllByModelNameIsNotNullAndUseYn('Y');
//
//        for (String pathImage : images) {
//            metadataList.forEach(metadata -> {
//                if (metadata.getPathImgGoods() != null && metadata.getPathImgGoods().contains(pathImage)) {
//                    metadataDtos.add(new GetMetadataDto(metadata, "photo"));
//                } else if (metadata.getDesignInfo().getImgPath() != null && metadata.getDesignInfo().getImgPath().contains(pathImage)) {
//                    metadataDtos.add(new GetMetadataDto(metadata, "drawing"));
//                }
//            });
//        }
//        return metadataDtos;
//    }

//    public void cancelExcessReserveTime() {
//        LocalDateTime now = LocalDateTime.now();
//
//        List<ModelInfo> reservedMetadataList = metadataRepository.findAllByReserveDateNotNull();
//
//        for (ModelInfo reservedMetadata : reservedMetadataList) {
//            if (ChronoUnit.SECONDS.between(reservedMetadata.getReserveDate(), now) > 172800) {
//                cancelReserveMetadata(reservedMetadata.getMetaSeq());
//            }
//        }
//    }

//    public void reserveMetadata(String memberId, int metaSeq) {
//        int reservedCount = metadataRepository.countByReserveId(memberId);
//        if (reservedCount >= 3)
//            throw new ExceededReservedCountLimitException();
//
//        ModelInfo metadata = metadataRepository.findById(metaSeq).orElseThrow(() -> new MetadataNotFoundException(metaSeq));
//
//        if (metadata.getReserveId().equals(memberId))
//            throw new DuplicationReserveIdException(metadata.getMetaSeq());
//
//        metadata.setReserveId(memberId);
//        metadata.setReserveDate(LocalDateTime.now());
//        metadataRepository.save(metadata);
//    }

//    public void cancelReserveMetadata(int metaSeq) {
//        ModelInfo metadata = metadataRepository.findById(metaSeq).orElseThrow(() -> new MetadataNotFoundException(metaSeq));
//        metadata.setReserveId(defaultReserveId);
//        metadata.setReserveDate(null);
//        metadataRepository.save(metadata);
//    }

//    @Transactional(readOnly = true)
//    public GetMetadataDto getMetadataDetail(int metaSeq, String memberId) {
//        ModelInfo modelInfo = modelRepository.findById(metaSeq).orElseThrow(() -> new MetadataNotFoundException(metaSeq));
//
//        List<DesignImages> designImages = designImagesRepository.findAllByDesignSeq(modelInfo.getDesignInfo().getDesignSeq());
//
//        return new GetMetadataDto(modelInfo, designImages);
//    }

//    @Transactional(readOnly = true)
//    public List<GetArticleFileDto> getMetadataImages(int metaSeq, String memberId) {
//        return articleFileRepository.findAllByBoardNameAndArticleIdAndIsDeletedAndRegId("METAIMG", metaSeq, "0", memberId).stream()
//                .map(GetArticleFileDto::new)
//                .collect(Collectors.toList());
//    }

//    public void uploadMetadata(String memberId, MetadataRequest metadataRequest) {
//        int displayOrder = 0;
//
//        Member member = memberRepository.findByMemberId(memberId).orElseThrow(MemberNotFoundException::new);
//
//        ModelInfo metadata = metadataRepository.findById(metadataRequest.getMetaSeq()).orElseThrow(() -> new MetadataNotFoundException(metadataRequest.getMetaSeq()));
//
//        if (metadata.getReserveId().equals(defaultReserveId)) {
//            displayOrder = articleFileRepository.findTopByArticleIdOrderByArticleFileIdDesc(metadataRequest.getMetaSeq()).getDisplayOrder() + 1;
//        } else {
//            metadata.startWork(member, defaultReserveId, defaultImageCount + metadata.getImgCount());
//            metadataRepository.save(metadata);
//        }
//
//        uploadMetadataImages(metadataRequest, metadata, memberId, displayOrder);
//    }

//    private void uploadMetadataImages(MetadataRequest metadataRequest, ModelInfo metadata, String memberId, int displayOrder) {
//        if (metadataRequest.getImages() != null) {
//            for (MultipartFile image : metadataRequest.getImages()) {
//                ArticleFile articleFile = imageService.uploadMetadataImage(memberId, metadata, image, displayOrder++);
//                articleFileRepository.save(articleFile);
//            }
//        }
//    }

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
