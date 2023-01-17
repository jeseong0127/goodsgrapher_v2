package com.vitasoft.goodsgrapher.domain.model.kipris.repository;

import com.vitasoft.goodsgrapher.domain.model.enums.BoardName;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Article;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
    List<Article> findAllByBoardNameAndIsDeletedOrderByArticleIdDesc(BoardName boardName, char isDeleted);

    Article findTopByBoardNameAndIsDeletedOrderByArticleIdDesc(BoardName boardName, char isDeleted);
}
