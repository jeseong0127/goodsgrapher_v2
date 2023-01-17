package com.vitasoft.goodsgrapher.domain.model.kipris.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "com_code")
@Getter
@Setter
@DynamicUpdate
@RequiredArgsConstructor
@IdClass(CodePK.class)
public class Code {

    @Id
    @Column(name = "HIGH_CODE_ID")
    private String highCodeId;

    @Id
    @Column(name = "CODE_ID")
    private String codeId;

    private String codeName;

    private char useYn;

    private String codeGroup;

}
