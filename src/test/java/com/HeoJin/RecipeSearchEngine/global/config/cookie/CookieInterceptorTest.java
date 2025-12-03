package com.HeoJin.RecipeSearchEngine.global.config.cookie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CookieInterceptorTest {

    @Test
    @DisplayName("UUID 문자열 길이가 36자인지 확인")
    void test1() {
        // given
        UUID uuid = UUID.randomUUID();

        // when
        String cmp = uuid.toString();

        // then
        assertThat(cmp)
                .hasSize(36)
                .contains("-");
    }
}
