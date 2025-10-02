package com.HeoJin.RecipeSearchEngine.basic.controller;


import com.HeoJin.RecipeSearchEngine.Init.TestService;
import com.HeoJin.RecipeSearchEngine.global.entity.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class BasicSearchControllerTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestService testService;

    @Value("${mongo.collectionName}")
    private String collectionName;

    @BeforeEach
    public void init() {
        if (mongoTemplate.count(new Query(), collectionName) == 0) {
            testService.insertInitData();
        }
    }

    @Test
    @DisplayName("단일 조회 RestDoc Test")
    void test1() throws Exception {
        // given
        Recipe testRecipe = mongoTemplate.findOne(new Query(), Recipe.class, collectionName);
        String id = testRecipe.getId(); // null 처리 할 필요 없는듯, 어처피 실패라

        // when + then
        ResultActions testMock = mockMvc.perform(get("/seo/basic/recipe")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("objectId", id))
                .andExpect(status().isOk())
                .andDo(print());

        // docs

        testMock.andDo(document("get-/seo/basic/recipe - single recipe select",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("objectId").description("찾고자 하는 objectId 명")
                ),
                responseFields(
                        fieldWithPath("objectId").description("레시피 고유 ID"),
                        fieldWithPath("recipeName").description("레시피 이름"),
                        fieldWithPath("sourceUrl").description("레시피 출처 URL"),
                        fieldWithPath("siteIndex").description("사이트 인덱스"),
                        fieldWithPath("ingredientList").description("재료 목록"),
                        fieldWithPath("ingredientList[]").description("재료"),
                        fieldWithPath("cookingOrderList").description("조리 순서 목록"),
                        fieldWithPath("cookingOrderList[].step").description("조리 단계"),
                        fieldWithPath("cookingOrderList[].instruction").description("조리 설명")
                )));
    }


    @Test
    @DisplayName("전체 보기  RestDoc Test")
    void test2() throws Exception {
        // given

        // when + then
        ResultActions testMock = mockMvc.perform(get("/seo/basic/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("page", String.valueOf(0))
                        .queryParam("pageSize", String.valueOf(3))
                        .queryParam("objectId", "")
                )
                .andExpect(status().isOk())
                .andDo(print());

        // docs

        testMock.andDo(document("get-/seo/basic/recipes - paging recipes select",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("page").description("현재 페이지와 차이나는 페이지 수 (default = 0)"),
                        parameterWithName("pageSize").description("페이지 사이즈"),
                        parameterWithName("objectId").description("현재 페이지의 마지막 objectId")
                ),
                responseFields(
                        fieldWithPath("recipes").description("레시피 목록"),
                        fieldWithPath("recipes[].objectId").description("레시피 고유 ID"),
                        fieldWithPath("recipes[].recipeName").description("레시피 이름"),
                        fieldWithPath("recipes[].sourceUrl").description("레시피 출처 URL"),
                        fieldWithPath("recipes[].siteIndex").description("사이트 인덱스"),
                        fieldWithPath("recipes[].ingredientList").description("재료 목록"),
                        fieldWithPath("recipes[].ingredientList[]").description("재료"),
                        fieldWithPath("recipes[].cookingOrderList").description("조리 순서 목록"),
                        fieldWithPath("recipes[].cookingOrderList[].step").description("조리 단계"),
                        fieldWithPath("recipes[].cookingOrderList[].instruction").description("조리 설명")
                )));
    }

    @Test
    @DisplayName("전체 recipe count 로직")
    void test3() throws Exception {
        // given

        // when + then
        ResultActions testMock = mockMvc.perform(get("/seo/basic/recipescount")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());

        // docs

        testMock.andDo(document("get-/seo/basic/recipescount - whole recipe count",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                     fieldWithPath("recipeCount").description("전체 레시피 갯수")
                )));
    }
}
