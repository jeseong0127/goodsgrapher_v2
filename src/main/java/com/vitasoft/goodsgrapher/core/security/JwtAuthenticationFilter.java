package com.vitasoft.goodsgrapher.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitasoft.goodsgrapher.core.response.ErrorResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            Claims claims = jwtTokenProvider.resolveToken((HttpServletRequest) request);
            if (claims != null)
                SecurityContextHolder.getContext().setAuthentication(jwtTokenProvider.getAuthentication(claims));
            filterChain.doFilter(request, response);
        } catch (SignatureException e) {
            sendErrorMessage((HttpServletResponse) response, "Auth-001", "유효하지 않은 토큰입니다.");
        } catch (MalformedJwtException e) {
            sendErrorMessage((HttpServletResponse) response, "Auth-002", "손상된 토큰입니다.");
        } catch (DecodingException e) {
            sendErrorMessage((HttpServletResponse) response, "Auth-003", "잘못된 인증입니다.");
        } catch (ExpiredJwtException e) {
            sendErrorMessage((HttpServletResponse) response, "Auth-004", "만료된 토큰입니다.");
        }
    }

    private void sendErrorMessage(HttpServletResponse response, String error, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.getWriter().write(objectMapper.writeValueAsString(new ErrorResponse(HttpStatus.FORBIDDEN, error, message)));
    }
}
