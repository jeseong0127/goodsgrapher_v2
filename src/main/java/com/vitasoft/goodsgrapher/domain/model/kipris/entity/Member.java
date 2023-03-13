package com.vitasoft.goodsgrapher.domain.model.kipris.entity;

import com.vitasoft.goodsgrapher.domain.model.enums.MemberRole;
import com.vitasoft.goodsgrapher.domain.model.sso.entity.IntegratedMember;

import java.time.Clock;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "goodsgrapher_members")
@Getter
@RequiredArgsConstructor
@DynamicInsert
public class Member {
    @Id
    private String memberId;

    private String memberPw;

    private String memberName;

    private String memberPhone;

    private String memberEmail;

    private String memberEmail2;

    private String memberAddress;

    private String memberAddress2;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    private String memberDept;

    private String memberBirth;

    private String memberRank;

    private String memberPosition;

    private String memberDescription;

    private String memberWriteYn;

    private char useYn;

    private char agreeYn;

    private LocalDateTime agreeDate;

    private char contractYn;

    private LocalDateTime contractDate;

    private String contractPath;

    private String association;

    public Member signUp(IntegratedMember integratedMember) {
        this.memberId = integratedMember.getMemberId();
        this.memberPw = integratedMember.getMemberPw();
        this.memberName = integratedMember.getMemberName();
        this.memberPhone = integratedMember.getMemberPhone();
        this.memberEmail = integratedMember.getMemberEmail();
        this.memberEmail2 = integratedMember.getMemberEmail2();
        this.memberAddress = integratedMember.getMemberAddress();
        this.memberAddress2 = integratedMember.getMemberAddress2();
        this.memberRole = MemberRole.R008;
        this.useYn = 'Y';
        this.agreeYn = 'N';
        this.contractYn = 'N';
        return this;
    }

    public void writeContract(String contractPath) {
        this.contractYn = 'Y';
        this.contractDate = LocalDateTime.now(Clock.systemDefaultZone());
        this.contractPath = contractPath;
    }

    public void writeAgreements() {
        this.agreeYn = 'Y';
        this.agreeDate = LocalDateTime.now(Clock.systemDefaultZone());
    }
}
