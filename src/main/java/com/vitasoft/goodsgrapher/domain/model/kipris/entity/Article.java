package com.vitasoft.goodsgrapher.domain.model.kipris.entity;

import com.vitasoft.goodsgrapher.domain.model.enums.BoardName;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "com_article")
@Getter
@Setter
@RequiredArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int articleId;

    @Enumerated(EnumType.STRING)
    private BoardName boardName;

    private String title;

    private String content;

    private char isDeleted;

    private String regId;

    private String regName;

    private LocalDateTime regDate;

    private int viewCount;
}
