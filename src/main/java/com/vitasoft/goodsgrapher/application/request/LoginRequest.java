package com.vitasoft.goodsgrapher.application.request;

import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginRequest {

    @NotNull
    private final String memberId;

    @NotNull
    private final String memberPw;
}
