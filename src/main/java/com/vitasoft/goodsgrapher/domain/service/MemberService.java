package com.vitasoft.goodsgrapher.domain.service;

import com.fasterxml.uuid.Generators;
import com.vitasoft.goodsgrapher.application.response.AccountDetailResponse;
import com.vitasoft.goodsgrapher.application.response.AccountsResponse;
import com.vitasoft.goodsgrapher.domain.exception.image.CannotUploadPdfException;
import com.vitasoft.goodsgrapher.domain.exception.member.MemberNotFoundException;
import com.vitasoft.goodsgrapher.domain.model.dto.GetAccountsDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetDesignImageDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetDesignInfoDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetMetadataDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetWorkedLogsDto;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignImage;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.DesignInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Member;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelImage;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ModelInfo;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Work;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.DesignImageRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.DesignInfoRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.MemberRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ModelImageRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ModelInfoRepository;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.WorkRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    @Value("${image.upload-path.pdfContract}")
    private String pdfContractPath;

    private final WorkRepository workRepository;

    private final ModelInfoRepository modelInfoRepository;

    private final ModelImageRepository modelImageRepository;

    private final DesignImageRepository designImageRepository;

    private final DesignInfoRepository designInfoRepository;

    private final MemberRepository memberRepository;

    private final ImageService imageService;

    @Transactional(readOnly = true)
    public List<GetMetadataDto> getMetadata(String memberId) {

        List<Work> workList = workRepository.findAllByRegId(memberId);

        List<ModelInfo> modelInfoList = new ArrayList<>();

        for (Work work : workList) {
            modelInfoList.add(modelInfoRepository.findById(work.getModelSeq()).orElse(new ModelInfo()));
        }

        return modelInfoList.stream()
                .map(dto -> {
                            int workedCount = modelImageRepository.countAllByRegIdAndModelSeqAndIsDeleted(memberId, dto.getModelSeq(), "0");
                            Work work = workRepository.findByRegIdAndModelSeq(memberId, dto.getModelSeq());
                            return new GetMetadataDto(dto, new GetDesignInfoDto(designInfoRepository.findByRegistrationNumber(dto.getRegistrationNumber()),
                                    designImageRepository.findAllByDesignSeqAndUseYn(designInfoRepository.findByRegistrationNumber(dto.getRegistrationNumber()).getDesignSeq(), "Y")
                                            .stream().map(designImage -> new GetDesignImageDto(designImage.getImgNumber(), designImage.getImgPath(), designImage.getUseYn(), designImage.getDesignImgSeq())).collect(Collectors.toList())
                            ), workedCount, work.getRegDate(), work.getStatus());
                        }
                )
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountsResponse getAccounts(String memberId) {
        List<Work> workList = workRepository.findAllByRegIdAndStatus(memberId, "5");
        List<GetAccountsDto> getAccountsDtoList = new ArrayList<>();

        workList.forEach(work -> {
            int workedCount = modelImageRepository.countAllByRegIdAndModelSeqAndIsDeleted(memberId, work.getModelSeq(), "0");
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

    @Transactional(readOnly = true)
    public List<GetWorkedLogsDto> getWorkedLogs(String memberId) {
        List<GetWorkedLogsDto> getWorkedLogsDtoList = new ArrayList<>();
        workRepository.findAllByRegIdOrderByRegDateDesc(memberId)
                .forEach(work ->
                        getWorkedLogsDtoList.add(new GetWorkedLogsDto(work, modelInfoRepository.findByModelSeq(work.getModelSeq())))
                );
        return getWorkedLogsDtoList;
    }

    public void writeContracts(String memberId, String base64) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(MemberNotFoundException::new);

        String fileName = Generators.timeBasedGenerator().generate().toString() + ".pdf";
        File pdf = new File(pdfContractPath, fileName);

        writeByteArrayToFile(pdf, base64);
        member.writeContract(pdf.getAbsolutePath());
    }

    private void writeByteArrayToFile(File pdf, String base64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            FileUtils.writeByteArrayToFile(pdf, bytes);
        } catch (IOException e) {
            throw new CannotUploadPdfException();
        }
    }

    public void writeAgreements(String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(MemberNotFoundException::new);

        member.writeAgreements();
    }

    public byte[] viewContracts(String memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(MemberNotFoundException::new);

        return imageService.viewImage(new File(member.getContractPath()));
    }

    public byte[] viewDefaultContracts() {
        String defaultPath = "/uploads/pdf_contract_worker/default.pdf";

        return imageService.viewImage(new File(defaultPath));
    }
}
