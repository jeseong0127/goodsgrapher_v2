package com.vitasoft.goodsgrapher.domain.service;

import com.vitasoft.goodsgrapher.application.request.LoginRequest;
import com.vitasoft.goodsgrapher.application.response.LoginResponse;
import com.vitasoft.goodsgrapher.application.response.MemberResponse;
import com.vitasoft.goodsgrapher.application.response.TokenResponse;
import com.vitasoft.goodsgrapher.core.security.JwtTokenProvider;
import com.vitasoft.goodsgrapher.core.util.Encoder;
import com.vitasoft.goodsgrapher.domain.exception.member.MemberNotFoundException;
import com.vitasoft.goodsgrapher.domain.exception.member.MemberNotUseException;
import com.vitasoft.goodsgrapher.domain.exception.member.NotMatchPasswordException;
import com.vitasoft.goodsgrapher.domain.model.sso.entity.Member;
import com.vitasoft.goodsgrapher.domain.model.sso.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final Encoder encoder;

    public LoginResponse login(LoginRequest loginRequest) throws NotMatchPasswordException {
        Member member = memberRepository.findByMemberId(loginRequest.getMemberId()).orElseThrow(MemberNotFoundException::new);

        if (member.getUseYn() == 'N')
            throw new MemberNotUseException();

        if (!encoder.encrypt("SHA-256", loginRequest.getMemberPw()).equals(member.getMemberPw()))
            throw new NotMatchPasswordException();

        MemberResponse memberResponse = new MemberResponse(member);

        String accessToken = jwtTokenProvider.generateJwtToken(member.getMemberId(), member.getMemberRole());
        TokenResponse tokenResponse = new TokenResponse(accessToken);

        return new LoginResponse(memberResponse, tokenResponse);
    }
}
