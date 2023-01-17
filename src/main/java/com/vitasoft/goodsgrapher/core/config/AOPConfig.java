package com.vitasoft.goodsgrapher.core.config;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class AOPConfig {
    @Before("execution(* *..controller.*.*(..))")
    public void recordRequestURI() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String queryString = request.getQueryString();
        log.info(request.getRequestURI() + (queryString == null ? "" : "?" + queryString));
    }

    @AfterThrowing(value = "execution(* *..controller.*.*(..))", throwing = "exception")
    public void logError(RuntimeException exception) {
        log.error(exception.getMessage());
    }
}
