package com.HeoJin.RecipeSearchEngine.guest.controller;

import com.HeoJin.RecipeSearchEngine.guest.dto.response.MessageResponseDto;
import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeBookmarkRequest;
import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeLikeRequest;
import com.HeoJin.RecipeSearchEngine.guest.dto.response.RecipeBookmarkListResponseDto;
import com.HeoJin.RecipeSearchEngine.guest.dto.response.RecipeLikeListResponseDto;
import com.HeoJin.RecipeSearchEngine.guest.dto.response.RecipeStatusDto;
import com.HeoJin.RecipeSearchEngine.guest.dto.response.RecipeStatusListResponseDto;
import com.HeoJin.RecipeSearchEngine.guest.service.GuestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class GuestControllerRestDocTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private GuestService guestService;
    @Value("${custom.guest.cookie.name}")
    private String GUEST_COOKIE_NAME;

    @Test
    @DisplayName("레시피 좋아요 추가 (토글) + 정상 동작")
    void test1() throws Exception {
        // given
        RecipeLikeRequest request = RecipeLikeRequest.builder()
                .recipeId("recipe-123")
                .build();
        
        when(guestService.toggleRecipeLike(any(RecipeLikeRequest.class), any()))
                .thenReturn(new MessageResponseDto("좋아요 목록에 추가되었습니다.")); // or "좋아요가 취소되었습니다."

        // when
        ResultActions result = mockMvc.perform(post("/seo/recipe/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .cookie(new Cookie(GUEST_COOKIE_NAME, "test-guest-uuid")));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("좋아요 목록에 추가되었습니다."))
                .andDo(print())
                .andDo(document("post-recipe-like",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName(GUEST_COOKIE_NAME).description("게스트 식별자 쿠키")
                        ),
                        requestFields(
                                fieldWithPath("recipeId").type(JsonFieldType.STRING).description("좋아요할 레시피 ID")
                        ),
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지 (추가/취소)")
                        )
                ));
    }

    @Test
    @DisplayName("레시피 북마크 추가 (토글)")
    void test2() throws Exception {
        // given
        RecipeBookmarkRequest request = RecipeBookmarkRequest.builder()
                .recipeId("recipe-123")
                .build();

        when(guestService.toggleBookmark(any(RecipeBookmarkRequest.class), any()))
                .thenReturn(new MessageResponseDto("북마크 목록에 추가되었습니다."));

        // when
        ResultActions result = mockMvc.perform(post("/seo/recipe/bookmark")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .cookie(new Cookie(GUEST_COOKIE_NAME, "test-guest-uuid")));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("북마크 목록에 추가되었습니다."))
                .andDo(print())
                .andDo(document("post-recipe-bookmark",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName(GUEST_COOKIE_NAME).description("게스트 식별자 쿠키 (선택)")
                        ),
                        requestFields(
                                fieldWithPath("recipeId").type(JsonFieldType.STRING).description("북마크할 레시피 ID")
                        ),
                        responseFields(
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지 (추가/취소)")
                        )
                ));
    }

    @Test
    @DisplayName("레시피 상태(좋아요, 북마크) 조회")
    void test3() throws Exception {
        // given
        List<String> recipeIds = Arrays.asList("recipe-1", "recipe-2");
        RecipeStatusListResponseDto responseDto = new RecipeStatusListResponseDto(
                Arrays.asList(
                        new RecipeStatusDto("recipe-1", true, false),
                        new RecipeStatusDto("recipe-2", false, true)
                )
        );

        when(guestService.getStatusWithRecipeList(any(), any()))
                .thenReturn(responseDto);

        // when
        ResultActions result = mockMvc.perform(get("/seo/recipe/status")
                .param("recipeId", "recipe-1")
                .param("recipeId", "recipe-2")
                .cookie(new Cookie(GUEST_COOKIE_NAME, "test-guest-uuid")));

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-recipe-status",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName(GUEST_COOKIE_NAME).description("게스트 식별자 쿠키 (선택)")
                        ),
                        queryParameters(
                                parameterWithName("recipeId").description("조회할 레시피 ID 리스트")
                        ),
                        responseFields(
                                fieldWithPath("recipeStatuses").type(JsonFieldType.ARRAY).description("레시피 상태 리스트"),
                                fieldWithPath("recipeStatuses[].recipeId").type(JsonFieldType.STRING).description("레시피 ID"),
                                fieldWithPath("recipeStatuses[].liked").type(JsonFieldType.BOOLEAN).description("좋아요 여부"),
                                fieldWithPath("recipeStatuses[].bookmarked").type(JsonFieldType.BOOLEAN).description("북마크 여부")
                        )
                ));
    }

    @Test
    @DisplayName("좋아요한 레시피 목록 조회")
    void test4() throws Exception {
        // given
        RecipeLikeListResponseDto responseDto = new RecipeLikeListResponseDto(
                Arrays.asList("recipe-1", "recipe-3")
        );

        when(guestService.getLikeList(any()))
                .thenReturn(responseDto);

        // when
        ResultActions result = mockMvc.perform(get("/seo/recipe/likes")
                .cookie(new Cookie(GUEST_COOKIE_NAME, "test-guest-uuid")));

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-recipe-likes",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName(GUEST_COOKIE_NAME).description("게스트 식별자 쿠키 (선택)")
                        ),
                        responseFields(
                                fieldWithPath("recipeIds").type(JsonFieldType.ARRAY).description("좋아요한 레시피 ID 리스트")
                        )
                ));
    }

    @Test
    @DisplayName("북마크한 레시피 목록 조회")
    void test5() throws Exception {
        // given
        RecipeBookmarkListResponseDto responseDto = new RecipeBookmarkListResponseDto(
                Arrays.asList("recipe-2", "recipe-4")
        );

        when(guestService.getBookmarkList(any()))
                .thenReturn(responseDto);

        // when
        ResultActions result = mockMvc.perform(get("/seo/recipe/bookmark")
                .cookie(new Cookie(GUEST_COOKIE_NAME, "test-guest-uuid")));

        // then
        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-recipe-bookmarks",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName(GUEST_COOKIE_NAME).description("게스트 식별자 쿠키 (선택)")
                        ),
                        responseFields(
                                fieldWithPath("recipeIds").type(JsonFieldType.ARRAY).description("북마크한 레시피 ID 리스트")
                        )
                ));
    }
}
