package com.vitasoft.goodsgrapher.application.controller;

import com.vitasoft.goodsgrapher.application.response.ArticleResponse;
import com.vitasoft.goodsgrapher.application.response.LatestNoticeResponse;
import com.vitasoft.goodsgrapher.domain.service.ArticleService;
import io.swagger.annotations.ApiOperation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @ApiOperation("공지사항 리스트 가져오기")
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public ArticleResponse getNotices(
    ) {
        return new ArticleResponse(articleService.getNotices());
    }

    @ApiOperation("최신 공지사항 가져오기")
    @GetMapping("/latest-notice")
    @ResponseStatus(HttpStatus.OK)
    public LatestNoticeResponse getLatestNotice(
    ) {
        return new LatestNoticeResponse(articleService.getLatestNotice());
    }

}
