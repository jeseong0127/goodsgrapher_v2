package com.vitasoft.goodsgrapher.domain.model.dto;

import com.vitasoft.goodsgrapher.domain.model.kipris.entity.ArticleFile;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class GetArticleFileDto {
    private final int articleFileId;
    private final String boardName;
    private final int articleId;
    private final int displayOrder;
    private final String displayName;
    private final String fileName;
    private final String fileSize;
    private final String fileType;
    private final int viewCount;
    private final String isDeleted;
    private final String regId;
    private final LocalDateTime regDate;
    private final char inspectPF;
    private final String etc;

    public GetArticleFileDto(ArticleFile articleFile) {
        this.articleFileId = articleFile.getArticleFileId();
        this.boardName = articleFile.getBoardName();
        this.articleId = articleFile.getArticleId();
        this.displayOrder = articleFile.getDisplayOrder();
        this.displayName = articleFile.getDisplayName();
        this.fileName = articleFile.getFileName();
        this.fileSize = articleFile.getFileSize();
        this.fileType = articleFile.getFileType();
        this.viewCount = articleFile.getViewCount();
        this.isDeleted = articleFile.getIsDeleted();
        this.regId = articleFile.getRegId();
        this.regDate = articleFile.getRegDate();
        this.inspectPF = articleFile.getInspectPf();
        this.etc = articleFile.getEtc();
    }
}
