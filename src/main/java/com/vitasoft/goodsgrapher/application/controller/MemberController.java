package com.vitasoft.goodsgrapher.application.controller;

import com.vitasoft.goodsgrapher.application.request.ContractRequest;
import com.vitasoft.goodsgrapher.application.response.AccountDetailResponse;
import com.vitasoft.goodsgrapher.application.response.AccountsResponse;
import com.vitasoft.goodsgrapher.application.response.MetadataResponse;
import com.vitasoft.goodsgrapher.application.response.WorkedLogsResponse;
import com.vitasoft.goodsgrapher.core.security.AuthenticatedMember;
import com.vitasoft.goodsgrapher.core.security.MemberInfo;
import com.vitasoft.goodsgrapher.domain.service.MemberService;
import io.swagger.annotations.ApiOperation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @ApiOperation("내 작업 리스트 가져오기")
    @GetMapping("/metadata")
    @ResponseStatus(HttpStatus.OK)
    public MetadataResponse getMetadata(
            @MemberInfo AuthenticatedMember member
    ) {
        return new MetadataResponse(memberService.getMetadata(member.getMemberId()));
    }

    @ApiOperation("내 정산목록 가져오기")
    @GetMapping("/accounts")
    @ResponseStatus(HttpStatus.OK)
    public AccountsResponse getAccounts(
            @MemberInfo AuthenticatedMember member
    ) {
        return memberService.getAccounts(member.getMemberId());
    }

    @ApiOperation("내 정산목록의 상세정보 가져오기")
    @GetMapping("/accounts/{modelSeq}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDetailResponse getAccountDetail(
            @MemberInfo AuthenticatedMember member,
            @PathVariable int modelSeq
    ) {
        return memberService.getAccountDetail(modelSeq, member.getMemberId());
    }

    @ApiOperation("내 작업 기록 보기")
    @GetMapping("/worked-logs")
    @ResponseStatus(HttpStatus.OK)
    public WorkedLogsResponse getWorkedLogs(
            @MemberInfo AuthenticatedMember member
    ) {
        return new WorkedLogsResponse(memberService.getWorkedLogs(member.getMemberId()));
    }

    @ApiOperation("내 계약서 작성하기")
    @PostMapping("/contracts")
    @ResponseStatus(HttpStatus.CREATED)
    public void writeContracts(
            @MemberInfo AuthenticatedMember member,
            @RequestBody ContractRequest request
    ) {
        memberService.writeContracts(member.getMemberId(), request.getBase64());
    }

    @ApiOperation("약관 동의하기")
    @PostMapping("/agreements")
    @ResponseStatus(HttpStatus.CREATED)
    public void writeAgreements(
            @MemberInfo AuthenticatedMember member
    ) {
        memberService.writeAgreements(member.getMemberId());
    }

    @ApiOperation("내 계약서 조회하기")
    @GetMapping(value = "/contracts", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] viewContracts(
            @MemberInfo AuthenticatedMember member
    ) {
        return memberService.viewContracts(member.getMemberId());
    }

    @ApiOperation("기본 계약서 조회하기")
    @GetMapping(value = "/default-contracts", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] viewDefaultContracts(
    ) {
        return memberService.viewDefaultContracts();
    }
}
