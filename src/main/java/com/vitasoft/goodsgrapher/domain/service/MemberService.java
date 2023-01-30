package com.vitasoft.goodsgrapher.domain.service;

import com.vitasoft.goodsgrapher.domain.model.dto.GetAccountsDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetMetadataDto;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Work;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.AdjustmentRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ModelInfoRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.WorkRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final AdjustmentRepository adjustmentRepository;

    private final WorkRepository workRepository;

    private final ModelInfoRepository modelInfoRepository;

    @Transactional(readOnly = true)
    public List<GetMetadataDto> getMetadata(String memberId) {

        List<Work> workList = workRepository.findAllByMetadata(memberId);

        List<ModelInfo> modelInfoList = new ArrayList<>();

        for (Work work : workList) {
            modelInfoList.add(modelInfoRepository.findById(work.getModelSeq()).orElse(new ModelInfo()));
        }

        return modelInfoList.stream()
                .map(GetMetadataDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GetAccountsDto> getAccounts(String memberId) {
        return adjustmentRepository.findByAdjustId(memberId).stream()
                .map(GetAccountsDto::new)
                .collect(Collectors.toList());
    }

//    @Transactional(readOnly = true)
//    public AccountDetailResponse getAccountDetail(int metaSeq, String memberId) {
//        ModelInfo metadata = metadataRepository.findById(metaSeq).orElseThrow(() -> new MetadataNotFoundException(metaSeq));
//
//        List<ArticleFile> articleFile = articleFileRepository.findAllByBoardNameAndArticleIdAndIsDeletedAndRegId("METAIMG", metaSeq, "0", memberId);
//
//        return new AccountDetailResponse(metadata, articleFile);
//    }
}
