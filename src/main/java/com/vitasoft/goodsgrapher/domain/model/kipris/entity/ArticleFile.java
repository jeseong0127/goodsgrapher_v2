package com.vitasoft.goodsgrapher.domain.model.kipris.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "com_article_file")
@Getter
@Setter
@DynamicUpdate
@RequiredArgsConstructor
public class ArticleFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int articleFileId;

    private String boardName = "METAIMG";

    private int articleId;

    private int displayOrder;

    private String displayName;

    private String fileName;

    private String fileSize;

    private String fileType;

    private int viewCount = 0;

    private String isDeleted = "0";

    private String regId;

    private LocalDateTime regDate;

    private char inspectPf = 'N';

    private String etc = "metaFile";

    private String etc2;

    public ArticleFile(int metaSeq, String displayName, String fileName, String fileSize, String fileType, String memberId, int displayOrder) {
        this.articleId = metaSeq;
        this.displayOrder = displayOrder;
        this.displayName = displayName;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.regId = memberId;
        this.regDate = LocalDateTime.now();
    }
}
