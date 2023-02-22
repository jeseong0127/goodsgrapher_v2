package com.vitasoft.goodsgrapher.domain.service;

import com.vitasoft.goodsgrapher.application.response.AccountsResponse;
import com.vitasoft.goodsgrapher.domain.model.dto.GetAccountsDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetMetadataDto;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Work;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.AdjustmentRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ModelImageRepository;
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

    private final ModelImageRepository modelImageRepository;

    @Transactional(readOnly = true)
    public List<GetMetadataDto> getMetadata(String memberId) {

        List<Work> workList = workRepository.findAllByRegIdAndStatus(memberId, "1");

        List<ModelInfo> modelInfoList = new ArrayList<>();

        for (Work work : workList) {
            modelInfoList.add(modelInfoRepository.findById(work.getModelSeq()).orElse(new ModelInfo()));
        }

        return modelInfoList.stream()
                .map(dto -> {
                            int workedCount = modelImageRepository.countAllByRegIdAndModelSeq(memberId, dto.getModelSeq());
                            Work work = workRepository.findByRegIdAndModelSeqAndStatus(memberId, dto.getModelSeq(), "1");
                            return new GetMetadataDto(dto, workedCount, work.getRegDate());
                        }
                )
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountsResponse getAccounts(String memberId) {
        List<Work> workList = workRepository.findAllByRegId(memberId);
        List<GetAccountsDto> getAccountsDtoList = new ArrayList<>();

        workList.forEach(work -> {
            int workedCount = modelImageRepository.countAllByRegIdAndModelSeq(memberId, work.getModelSeq());
            getAccountsDtoList.add(new GetAccountsDto(work, workedCount));
        });

        return new AccountsResponse(getAccountsDtoList);
    }

//    @Transactional(readOnly = true)
//    public AccountDetailResponse getAccountDetail(int modelSeq, String memberId) {
//        ModelInfo metadata = metadataRepository.findById(metaSeq).orElseThrow(() -> new MetadataNotFoundException(metaSeq));
//
//        List<ArticleFile> articleFile = articleFileRepository.findAllByBoardNameAndArticleIdAndIsDeletedAndRegId("METAIMG", metaSeq, "0", memberId);
//
//        return new AccountDetailResponse(metadata, articleFile);

//    }
}
