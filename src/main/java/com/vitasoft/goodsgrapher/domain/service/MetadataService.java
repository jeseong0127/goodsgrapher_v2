package com.vitasoft.goodsgrapher.domain.service;

import com.vitasoft.goodsgrapher.application.request.DeleteMetadataRequest;
import com.vitasoft.goodsgrapher.application.request.MetadataRequest;
import com.vitasoft.goodsgrapher.domain.exception.member.MemberNotFoundException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.ArticleFileNotFoundException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.DuplicationReserveIdException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.ExceededReservedCountLimitException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.MetadataNotFoundException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.RegIdIsNotWorkerException;
import com.vitasoft.goodsgrapher.domain.model.dto.GetArticleFileDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetCategoryDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetMetadataDto;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ArticleFile;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Metadata;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ArticleFileRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.CodeRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.MetadataRepository;
import com.vitasoft.goodsgrapher.domain.model.sso.entity.Member;
import com.vitasoft.goodsgrapher.domain.model.sso.repository.MemberRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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

    private final MetadataRepository metadataRepository;

    private final MemberRepository memberRepository;

    private final CodeRepository codeRepository;

    private final String defaultReserveId = "N/A";

    private static final int defaultImageCount = 62;

    public List<GetMetadataDto> getMetadataList() {
        cancelExcessReserveTime();

        return metadataRepository.findAllByImgCountLessThan(62).stream()
                .map(GetMetadataDto::new)
                .collect(Collectors.toList());
    }

    public List<GetMetadataDto> getSearchMetadata(String searchWord, String codeId) {
        cancelExcessReserveTime();

        return metadataRepository.findAllByMetadata(codeId, searchWord == null ? "" : searchWord, searchWord, searchWord).stream()
                .map(GetMetadataDto::new)
                .collect(Collectors.toList());
    }

    public List<GetMetadataDto> getImageSearchMetadata(List<String> images) {
        cancelExcessReserveTime();
        List<GetMetadataDto> metadataDtos = new ArrayList<>();

        List<Metadata> metadataList = metadataRepository.findAllByModelNameIsNotNullAndReserveIdAndRegIdNullAndImgCountLessThanAndUseYn(defaultReserveId, 62, 'Y');

        for (String pathImage : images) {
            metadataList.forEach(metadata -> {
                if (metadata.getPathImgGoods() != null && metadata.getPathImgGoods().contains(pathImage)) {
                    metadataDtos.add(new GetMetadataDto(metadata, "photo"));
                } else if (metadata.getPathImg() != null && metadata.getPathImg().contains(pathImage)) {
                    metadataDtos.add(new GetMetadataDto(metadata, "drawing"));
                }
            });
        }
        return metadataDtos;
    }

    public void cancelExcessReserveTime() {
        LocalDateTime now = LocalDateTime.now();

        List<Metadata> reservedMetadataList = metadataRepository.findAllByReserveDateNotNull();

        for (Metadata reservedMetadata : reservedMetadataList) {
            if (ChronoUnit.SECONDS.between(reservedMetadata.getReserveDate(), now) > 172800) {
                cancelReserveMetadata(reservedMetadata.getMetaSeq());
            }
        }
    }

    public void reserveMetadata(String memberId, int metaSeq) {
        int reservedCount = metadataRepository.countByReserveId(memberId);
        if (reservedCount >= 3)
            throw new ExceededReservedCountLimitException();

        Metadata metadata = metadataRepository.findById(metaSeq).orElseThrow(() -> new MetadataNotFoundException(metaSeq));

        if (metadata.getReserveId().equals(memberId))
            throw new DuplicationReserveIdException(metadata.getMetaSeq());

        metadata.setReserveId(memberId);
        metadata.setReserveDate(LocalDateTime.now());
        metadataRepository.save(metadata);
    }

    public void cancelReserveMetadata(int metaSeq) {
        Metadata metadata = metadataRepository.findById(metaSeq).orElseThrow(() -> new MetadataNotFoundException(metaSeq));
        metadata.setReserveId(defaultReserveId);
        metadata.setReserveDate(null);
        metadataRepository.save(metadata);
    }

    @Transactional(readOnly = true)
    public GetMetadataDto getMetadataDetail(int metaSeq, String memberId) {
        return new GetMetadataDto(metadataRepository.findById(metaSeq).orElseThrow(() -> new MetadataNotFoundException(metaSeq)), articleFileRepository.countByArticleIdAndRegIdAndIsDeleted(metaSeq, memberId, "0"));
    }

    @Transactional(readOnly = true)
    public List<GetArticleFileDto> getMetadataImages(int metaSeq, String memberId) {
        return articleFileRepository.findAllByBoardNameAndArticleIdAndIsDeletedAndRegId("METAIMG", metaSeq, "0", memberId).stream()
                .map(GetArticleFileDto::new)
                .collect(Collectors.toList());
    }

    public void uploadMetadata(String memberId, MetadataRequest metadataRequest) {
        int displayOrder = 0;

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(MemberNotFoundException::new);

        Metadata metadata = metadataRepository.findById(metadataRequest.getMetaSeq()).orElseThrow(() -> new MetadataNotFoundException(metadataRequest.getMetaSeq()));

        if (metadata.getReserveId().equals(defaultReserveId)) {
            displayOrder = articleFileRepository.findTopByArticleIdOrderByArticleFileIdDesc(metadataRequest.getMetaSeq()).getDisplayOrder() + 1;
        } else {
            metadata.startWork(member, defaultReserveId, defaultImageCount + metadata.getImgCount());
            metadataRepository.save(metadata);
        }

        uploadMetadataImages(metadataRequest, metadata, memberId, displayOrder);
    }

    private void uploadMetadataImages(MetadataRequest metadataRequest, Metadata metadata, String memberId, int displayOrder) {
        if (metadataRequest.getImages() != null) {
            for (MultipartFile image : metadataRequest.getImages()) {
                ArticleFile articleFile = imageService.uploadMetadataImage(memberId, metadata, image, displayOrder++);
                articleFileRepository.save(articleFile);
            }
        }
    }

    public void updateMetadata(String memberId, MetadataRequest metadataRequest) {

        Metadata metadata = metadataRepository.findById(metadataRequest.getMetaSeq()).orElseThrow(() -> new MetadataNotFoundException(metadataRequest.getMetaSeq()));

        int maxDisplayOrder = articleFileRepository.findTopByArticleIdOrderByArticleFileIdDesc(metadataRequest.getMetaSeq()).getDisplayOrder();

        for (int i = 0; i < metadataRequest.getImages().size(); i++) {
            ArticleFile articleFile = imageService.uploadMetadataImage(memberId, metadata, metadataRequest.getImages().get(i), maxDisplayOrder + (i + 1));
            articleFileRepository.save(articleFile);
        }
    }

    public void deleteMetadata(String memberId, DeleteMetadataRequest deleteMetadataRequest) throws IOException {
        metadataRepository.findByMetaSeqAndRegId(deleteMetadataRequest.getMetaSeq(), memberId)
                .orElseThrow(RegIdIsNotWorkerException::new);

        for (int articleFileId : deleteMetadataRequest.getArticleFileId()) {
            ArticleFile articleFile = articleFileRepository.findById(articleFileId)
                    .orElseThrow(ArticleFileNotFoundException::new);
            articleFile.setIsDeleted("1");
            articleFileRepository.save(articleFile);
            imageService.deleteMetadataImage(articleFile.getFileName());
        }
    }

    public void deleteWorkedMetadata(int metaSeq, String memberId) throws IOException {
        Metadata metadata = metadataRepository.findById(metaSeq).orElseThrow(() -> new MetadataNotFoundException(metaSeq));

        List<ArticleFile> articleFiles = articleFileRepository.findByArticleIdAndRegIdAndIsDeletedAndEtc2IsNull(metaSeq, memberId, "0");
        articleFiles.forEach(articleFile -> articleFile.setIsDeleted("1"));
        articleFileRepository.saveAll(articleFiles);

        metadata.deleteMetadata();

        for (ArticleFile articleFile : articleFiles) {
            imageService.deleteMetadataImage(articleFile.getFileName());
        }
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
