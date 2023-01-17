package com.vitasoft.goodsgrapher.domain.model.dto;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Article;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class GetNoticeDto {
    private final int articleId;
    private final String boardName;
    private final String title;
    private final String content;
    private final char isDeleted;
    private final String regId;
    private final String regName;
    private final LocalDateTime regDate;

    public GetNoticeDto(Article article) {
        this.articleId = article.getArticleId();
        this.boardName = String.valueOf(article.getBoardName());
        this.title = article.getTitle();
        this.content = article.getContent();
        this.isDeleted = article.getIsDeleted();
        this.regId = article.getRegId();
        this.regName = article.getRegName();
        this.regDate = article.getRegDate();
    }
}
