package com.vitasoft.goodsgrapher.domain.service;

import com.vitasoft.goodsgrapher.application.request.LoginRequest;
import com.vitasoft.goodsgrapher.application.response.LoginResponse;
import com.vitasoft.goodsgrapher.application.response.MemberResponse;
import com.vitasoft.goodsgrapher.application.response.TokenResponse;
import com.vitasoft.goodsgrapher.core.security.JwtTokenProvider;
import com.vitasoft.goodsgrapher.core.util.Encoder;
import com.vitasoft.goodsgrapher.domain.exception.member.IntegratedMemberNotFoundException;
import com.vitasoft.goodsgrapher.domain.exception.member.MemberNotUseException;
import com.vitasoft.goodsgrapher.domain.exception.member.NotMatchPasswordException;
import com.vitasoft.goodsgrapher.domain.model.kipris.entity.Member;
import com.vitasoft.goodsgrapher.domain.model.kipris.repository.MemberRepository;
import com.vitasoft.goodsgrapher.domain.model.sso.entity.IntegratedMember;
import com.vitasoft.goodsgrapher.domain.model.sso.repository.IntegratedMemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final IntegratedMemberRepository integratedMemberRepository;

    private final MemberRepository memberRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final Encoder encoder;

    public LoginResponse login(LoginRequest loginRequest) throws NotMatchPasswordException {
        IntegratedMember integratedMember = integratedMemberRepository.findByMemberId(loginRequest.getMemberId()).orElseThrow(IntegratedMemberNotFoundException::new);

        if (!encoder.encrypt("SHA-256", loginRequest.getMemberPw()).equals(integratedMember.getMemberPw()))
            throw new NotMatchPasswordException();

        Member member = memberRepository.findByMemberId(integratedMember.getMemberId())
                .orElseGet(() -> memberRepository.save(new Member().signUp(integratedMember)));

        if (member.getUseYn() == 'N')
            throw new MemberNotUseException();

        MemberResponse memberResponse = new MemberResponse(member);

        String accessToken = jwtTokenProvider.generateJwtToken(member.getMemberId(), member.getMemberRole(), member.getAgreeYn(), member.getContractYn());
        TokenResponse tokenResponse = new TokenResponse(accessToken);

        return new LoginResponse(memberResponse, tokenResponse);
    }
}
