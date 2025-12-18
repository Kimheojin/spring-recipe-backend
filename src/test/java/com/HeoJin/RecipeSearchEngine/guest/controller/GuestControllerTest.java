package com.HeoJin.RecipeSearchEngine.guest.controller;

import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeLikeRequest;
import com.HeoJin.RecipeSearchEngine.guest.service.GuestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GuestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GuestService guestService;

    @Value("${custom.guest.cookie.name}")
    private String GUEST_COOKIE_NAME;

    @Test
    @DisplayName("인터셉터 작동 확인: 쿠키 없이 요청 시 쿠키 생성 여부 테스트")
    void test1() throws Exception {
        // given
        RecipeLikeRequest request = RecipeLikeRequest.builder()
                .recipeId("test-recipe-id")
                .build();
        // service call mocking
        when(guestService.addRecipeLike(any(RecipeLikeRequest.class), any()))
                .thenReturn("ok");

        // when
        // 쿠키 없이 요청을 보냄
        ResultActions actions = mockMvc.perform(post("/seo/recipe/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        actions.andExpect(status().isOk())
               .andExpect(cookie().exists(GUEST_COOKIE_NAME)) 
               .andDo(print());
    }
}