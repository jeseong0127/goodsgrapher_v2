package com.vitasoft.goodsgrapher.application.response;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberResponse {
    private final String memberId;

    private final String memberName;

    private final String memberPhone;

    private final char agreeYn;

    private final char contactYn;

    public MemberResponse(Member member) {
        this.memberId = member.getMemberId();
        this.memberName = member.getMemberName();
        this.memberPhone = member.getMemberPhone();
        this.agreeYn = member.getAgreeYn();
        this.contactYn = member.getContractYn();
    }
}
