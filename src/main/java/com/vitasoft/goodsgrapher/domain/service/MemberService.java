package com.vitasoft.goodsgrapher.domain.service;

import com.vitasoft.goodsgrapher.application.response.AccountDetailResponse;
import com.vitasoft.goodsgrapher.application.response.AccountsResponse;
import com.vitasoft.goodsgrapher.domain.model.dto.GetAccountsDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetMetadataDto;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignImage;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelImage;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Work;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.AdjustmentRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.DesignImageRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.DesignInfoRepository;
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

    private final WorkRepository workRepository;

    private final ModelInfoRepository modelInfoRepository;

    private final ModelImageRepository modelImageRepository;

    private final DesignImageRepository designImageRepository;

    private final DesignInfoRepository designInfoRepository;

    @Transactional(readOnly = true)
    public List<GetMetadataDto> getMetadata(String memberId) {

        List<Work> workList = workRepository.findAllByRegId(memberId);

        List<ModelInfo> modelInfoList = new ArrayList<>();

        for (Work work : workList) {
            modelInfoList.add(modelInfoRepository.findById(work.getModelSeq()).orElse(new ModelInfo()));
        }

        return modelInfoList.stream()
                .map(dto -> {
                            int workedCount = modelImageRepository.countAllByRegIdAndModelSeq(memberId, dto.getModelSeq());
                            Work work = workRepository.findByRegIdAndModelSeq(memberId, dto.getModelSeq());
                            return new GetMetadataDto(dto, workedCount, work.getRegDate(), work.getStatus());
                        }
                )
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountsResponse getAccounts(String memberId) {
        List<Work> workList = workRepository.findAllByRegIdAndStatus(memberId, "5");
        List<GetAccountsDto> getAccountsDtoList = new ArrayList<>();

        workList.forEach(work -> {
            int workedCount = modelImageRepository.countAllByRegIdAndModelSeq(memberId, work.getModelSeq());
            getAccountsDtoList.add(new GetAccountsDto(work, workedCount));
        });

        return new AccountsResponse(getAccountsDtoList);
    }

    @Transactional(readOnly = true)
    public AccountDetailResponse getAccountDetail(int modelSeq, String memberId) {
        List<ModelImage> modelImages = modelImageRepository.findAllByModelSeqAndRegId(modelSeq, memberId);
        DesignInfo designInfo;
        List<DesignImage> designImages = new ArrayList<>();
        if (!modelImages.isEmpty()) {
            designInfo = designInfoRepository.findByRegistrationNumber(modelImages.get(0).getRegistrationNumber());
            designImages = designImageRepository.findAllByDesignSeqAndUseYn(designInfo.getDesignSeq(), "Y");
        }
        return new AccountDetailResponse(designImages, modelImages);
    }
}
