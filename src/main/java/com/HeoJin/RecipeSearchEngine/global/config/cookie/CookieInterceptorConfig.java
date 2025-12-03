package com.HeoJin.RecipeSearchEngine.global.config.cookie;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@RequiredArgsConstructor
public class CookieInterceptorConfig implements WebMvcConfigurer {
    private final CookieInterceptor cookieInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry ){
        registry.addInterceptor(cookieInterceptor)
                .addPathPatterns("/seo/**");
    }
}
