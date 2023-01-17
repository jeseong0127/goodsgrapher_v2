package com.vitasoft.goodsgrapher.application.exception;

import com.vitasoft.goodsgrapher.core.response.ErrorResponse;
import com.vitasoft.goodsgrapher.domain.exception.metadata.ArticleFileNotFoundException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.DuplicationReserveIdException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.ExceededReservedCountLimitException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.ExistsWorkedMetadataException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.MetadataNotFoundException;
import com.vitasoft.goodsgrapher.domain.exception.metadata.RegIdIsNotWorkerException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MetadataExceptionHandler {
    @ExceptionHandler(MetadataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleMetadataNotFound(MetadataNotFoundException exception) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, "Metadata-001", exception.getMessage());
    }

    @ExceptionHandler(ExceededReservedCountLimitException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleExceededReservedCountLimit(ExceededReservedCountLimitException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT, "Metadata-002", exception.getMessage());
    }

    @ExceptionHandler(ExistsWorkedMetadataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleExistsWorkedMetadata(ExistsWorkedMetadataException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT, "Metadata-003", exception.getMessage());
    }

    @ExceptionHandler(RegIdIsNotWorkerException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleRegIdIsNotWorker(RegIdIsNotWorkerException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT, "Metadata-004", exception.getMessage());
    }

    @ExceptionHandler(ArticleFileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleArticleFileNotFound(ArticleFileNotFoundException exception) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, "Metadata-005", exception.getMessage());
    }

    @ExceptionHandler(DuplicationReserveIdException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicationReserveId(DuplicationReserveIdException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT, "Metadata-006", exception.getMessage());
    }
}
