package com.vitasoft.goodsgrapher.domain.model.sso.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "com_members")
@Getter
@RequiredArgsConstructor
public class IntegratedMember {
    @Id
    private String memberId;

    private String memberPw;

    private String memberName;

    private String memberPhone;

    private String memberEmail;

    private String memberEmail2;

    private String memberAddress;

    private String memberAddress2;
}
