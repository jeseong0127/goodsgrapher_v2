package com.vitasoft.goodsgrapher.application.controller;

import com.vitasoft.goodsgrapher.application.response.AccountDetailResponse;
import com.vitasoft.goodsgrapher.application.response.AccountsResponse;
import com.vitasoft.goodsgrapher.application.response.MetadataResponse;
import com.vitasoft.goodsgrapher.core.security.AuthenticatedMember;
import com.vitasoft.goodsgrapher.core.security.MemberInfo;
import com.vitasoft.goodsgrapher.domain.service.MemberService;
import io.swagger.annotations.ApiOperation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        return new AccountsResponse(memberService.getAccounts(member.getMemberId()));
    }

    @ApiOperation("내 정산목록의 상세정보 가져오기")
    @GetMapping("/accounts/{metaSeq}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDetailResponse getAccountDetail(
            @MemberInfo AuthenticatedMember member,
            @PathVariable int metaSeq
    ) {
        return memberService.getAccountDetail(metaSeq, member.getMemberId());
    }
}
