package com.vitasoft.goodsgrapher.core.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthenticatedMember {
    private final String memberId;

    private final String memberRole;
}
