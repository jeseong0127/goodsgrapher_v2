package com.vitasoft.goodsgrapher.core.security;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class MemberInfoArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(MemberInfo.class) != null && parameter.getParameterType().equals(AuthenticatedMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest webRequest, WebDataBinderFactory webDataBinderFactory) {
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authenticationToken.getPrincipal().toString();
        return new AuthenticatedMember(memberId, mapRolesFromAuthorities((Collection<GrantedAuthority>) authenticationToken.getAuthorities()));
    }

    private String mapRolesFromAuthorities(Collection<GrantedAuthority> authorities) {
        Iterator<GrantedAuthority> it = authorities.iterator();
        GrantedAuthority grantedAuthority = it.next();
        return grantedAuthority.getAuthority();
    }
}
