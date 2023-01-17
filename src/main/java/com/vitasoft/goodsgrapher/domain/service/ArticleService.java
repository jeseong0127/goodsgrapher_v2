package com.vitasoft.goodsgrapher.domain.service;

import com.vitasoft.goodsgrapher.domain.model.dto.GetArticleDto;
import com.vitasoft.goodsgrapher.domain.model.dto.GetNoticeDto;
import com.vitasoft.goodsgrapher.domain.model.enums.BoardName;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.ArticleRepository;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public List<GetArticleDto> getNotices() {
        return articleRepository.findAllByBoardNameAndIsDeletedOrderByArticleIdDesc(BoardName.NOTICE, 'N').stream()
                .map(GetArticleDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GetNoticeDto getLatestNotice() {
        return new GetNoticeDto(articleRepository.findTopByBoardNameAndIsDeletedOrderByArticleIdDesc(BoardName.NOTICE, 'N'));
    }
}
