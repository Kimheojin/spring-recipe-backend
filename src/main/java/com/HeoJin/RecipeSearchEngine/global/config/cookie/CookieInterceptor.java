package com.HeoJin.RecipeSearchEngine.global.config.cookie;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.UUID;

@Component
@Slf4j
public class CookieInterceptor implements HandlerInterceptor {

    @Value("${custom.guest.cookie.name}")
    private String GUEST_COOKIE_NAME;
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        // 쿠키 꺼내기
        Cookie[] cookies = request.getCookies();

        boolean hasUuidCookie = false;

        if (cookies != null) {
            // 일치할경우 true 나옴
            hasUuidCookie = Arrays.stream(cookies)
                    .anyMatch(cookie -> cookie.getName().equals(GUEST_COOKIE_NAME));
        }

        if(!hasUuidCookie){
            // 이거 36자
            String uuid = UUID.randomUUID().toString();

            Cookie newCookie = new Cookie(GUEST_COOKIE_NAME, uuid);

            newCookie.setPath("/");
            newCookie.setHttpOnly(true);
            // 유횻기간, 초단위
            newCookie.setMaxAge(60 * 60 * 24 * 60);
            // HTTPS
            newCookie.setSecure(true);

            log.info("새 UUID 쿠키 생성: " + uuid);

        }
        // true 일 경우 Controller
        return true;

    }
}
