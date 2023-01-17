package com.vitasoft.goodsgrapher.application.exception;

import com.vitasoft.goodsgrapher.core.response.ErrorResponse;
import com.vitasoft.goodsgrapher.domain.exception.member.MemberNotFoundException;
import com.vitasoft.goodsgrapher.domain.exception.member.MemberNotUseException;
import com.vitasoft.goodsgrapher.domain.exception.member.NotMatchPasswordException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MemberExceptionHandler {
    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleMemberNotFound(MemberNotFoundException exception) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, "Member-001", exception.getMessage());
    }

    @ExceptionHandler(NotMatchPasswordException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleNotMatchPassword(NotMatchPasswordException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT, "Member-002", exception.getMessage());
    }

    @ExceptionHandler(MemberNotUseException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleNotNotUseMember(MemberNotUseException exception) {
        return new ErrorResponse(HttpStatus.FORBIDDEN, "Member-003", exception.getMessage());
    }
}
