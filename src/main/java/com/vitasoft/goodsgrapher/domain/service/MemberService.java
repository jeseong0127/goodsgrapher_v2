package com.vitasoft.goodsgrapher.domain.service;

import com.vitasoft.goodsgrapher.application.response.AccountDetailResponse;
import com.vitasoft.goodsgrapher.application.response.AccountsResponse;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSS");

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

    public void writeContracts(String memberId, MultipartFile file) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(MemberNotFoundException::new);

        LocalDateTime now = LocalDateTime.now();
        String fileName = uploadPdf(file, now);
        member.writeContract(pdfContractPath + File.separator + fileName, now);
    }

    public String uploadPdf(MultipartFile file, LocalDateTime now) {
        String date = now.format(dateFormatter);
        String time = now.format(dateTimeFormatter);
        String fileType = "." + FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName = date + "/" + time + fileType;
        imageService.uploadImage(pdfContractPath, file, fileName);
        return fileName;
    }
}
