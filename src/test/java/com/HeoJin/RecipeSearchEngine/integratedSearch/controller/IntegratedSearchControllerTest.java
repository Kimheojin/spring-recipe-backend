package com.HeoJin.RecipeSearchEngine.integratedSearch.controller;


import com.HeoJin.RecipeSearchEngine.Init.TestService;
import com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto.SearchRecipeListResponseDto;
import com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto.SearchRecipeResponseDto;
import com.HeoJin.RecipeSearchEngine.IntegratedSearch.repository.IntegrationSearchRepository;
import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeResponseDto;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
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
public class IntegratedSearchControllerTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestService testService;

    @Autowired
    private MockMvc mockMvc;

    @Value("${mongo.collectionName}")
    private String collectionName;

    @MockitoBean
    private IntegrationSearchRepository integrationSearchRepository;


    private static SearchRecipeListResponseDto mockResult;

    @BeforeEach
    void init() {
        // 데이터 없는 경우에만 삽입
        if (mongoTemplate.count(new Query(), collectionName) == 0) {
            testService.insertInitData();
        }
        // 이 부분 변경해야 할 듯
        if (mockResult == null) {
            List<SearchRecipeResponseDto> cmp = mongoTemplate.find(new Query(), Recipe.class, collectionName)
                .stream()
                .limit(10)
                .map(recipe -> SearchRecipeResponseDto.from(recipe, 0.0))
                .collect(Collectors.toList());

            mockResult = SearchRecipeListResponseDto.builder()
                    .totalPages(10)
                    .totalPages(1)
                    .currentPage(1)
                    .pageSize(10)
                    .recipes(cmp).build();
        }
    }

    @Test
    @DisplayName("재료명 기반 통합 검색")
    void test1() throws Exception {
        // given
        int page = 1;
        int pageSize = 10;
        String term = "고구마";

        when(integrationSearchRepository.getIngredientResult(page, pageSize, term))
                .thenReturn(mockResult);

        // when + then
        ResultActions testMock = mockMvc.perform(get("/seo/search/ingredient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("page", String.valueOf(page))
                        .queryParam("pageSize", String.valueOf(pageSize))
                        .queryParam("term", term))
                .andExpect(status().isOk())
                .andDo(print());

        // docs

        testMock.andDo(document("get-/seo/search/ingredient ingredient based integrated search test",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("page").description("검색하고자 하는 페이지"),
                        parameterWithName("pageSize").description("검색하고자 하는 페이지 사이즈"),
                        parameterWithName("term").description("검색하고자 하는 단어")
                ),
                responseFields(
                        fieldWithPath("totalCount").description("검색 결과 갯수"),
                        fieldWithPath("totalPages").description("전체 페이지 갯수"),
                        fieldWithPath("currentPage").description("현재 페이지 번호"),
                        fieldWithPath("pageSize").description("현재 페이지 사이즈"),
                        fieldWithPath("recipes").description("검색 결과 리스트"),
                        fieldWithPath("recipes[].objectId").description("레시피 ID"),
                        fieldWithPath("recipes[].recipeName").description("레시피 이름"),
                        fieldWithPath("recipes[].sourceUrl").description("레시피 Url"),
                        fieldWithPath("recipes[].siteIndex").description("레시피 해당 site 인덱스"),
                        fieldWithPath("recipes[].score").description("해당 레시피 가중치(여기선 사용 X)"),
                        fieldWithPath("recipes[].ingredientList").description("재료 리스트"),
                        fieldWithPath("recipes[].cookingOrderList").description("요리 순서"),
                        fieldWithPath("recipes[].cookingOrderList[].step").description("요리 순서 단계"),
                        fieldWithPath("recipes[].cookingOrderList[].instruction").description("요리 순서 설명")
                )));



    }

    @Test
    @DisplayName("cookingOrderList 기반 통합 검색")
    void test2() throws Exception {
        // given
        int page = 1;
        int pageSize = 10;
        String term = "고구마";

        when(integrationSearchRepository.getCookingOrderResult(page, pageSize, term))
                .thenReturn(mockResult);

        // when + then
        ResultActions testMock = mockMvc.perform(get("/seo/search/cookingorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("page", String.valueOf(page))
                        .queryParam("pageSize", String.valueOf(pageSize))
                        .queryParam("term", term))
                .andExpect(status().isOk())
                .andDo(print());

        // docs
        testMock.andDo(document("get-/seo/search/cookingorder cookingorder based integrated search test",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("page").description("검색하고자 하는 페이지"),
                        parameterWithName("pageSize").description("검색하고자 하는 페이지 사이즈"),
                        parameterWithName("term").description("검색하고자 하는 단어")
                ),
                responseFields(
                        fieldWithPath("totalCount").description("검색 결과 갯수"),
                        fieldWithPath("totalPages").description("전체 페이지 갯수"),
                        fieldWithPath("currentPage").description("현재 페이지 번호"),
                        fieldWithPath("pageSize").description("현재 페이지 사이즈"),
                        fieldWithPath("recipes").description("검색 결과 리스트"),
                        fieldWithPath("recipes[].objectId").description("레시피 ID"),
                        fieldWithPath("recipes[].recipeName").description("레시피 이름"),
                        fieldWithPath("recipes[].sourceUrl").description("레시피 Url"),
                        fieldWithPath("recipes[].siteIndex").description("레시피 해당 site 인덱스"),
                        fieldWithPath("recipes[].score").description("해당 레시피 가중치(여기선 사용 X)"),
                        fieldWithPath("recipes[].ingredientList").description("재료 리스트"),
                        fieldWithPath("recipes[].cookingOrderList").description("요리 순서"),
                        fieldWithPath("recipes[].cookingOrderList[].step").description("요리 순서 단계"),
                        fieldWithPath("recipes[].cookingOrderList[].instruction").description("요리 순서 설명")
                )));
    }

    @Test
    @DisplayName("레시피명 기반 통합 검색")
    void test3() throws Exception {
        // given
        int page = 1;
        int pageSize = 10;
        String term = "고구마";

        when(integrationSearchRepository.getRecipeNameResult(page, pageSize, term))
                .thenReturn(mockResult);

        // when + then
        ResultActions testMock = mockMvc.perform(get("/seo/search/recipename")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("page", String.valueOf(page))
                        .queryParam("pageSize", String.valueOf(pageSize))
                        .queryParam("term", term))
                .andExpect(status().isOk())
                .andDo(print());

        // docs

        testMock.andDo(document("get-/seo/search/recipename recipename based integrated search test",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("page").description("검색하고자 하는 페이지"),
                        parameterWithName("pageSize").description("검색하고자 하는 페이지 사이즈"),
                        parameterWithName("term").description("검색하고자 하는 단어")
                ),
                responseFields(
                        fieldWithPath("totalCount").description("검색 결과 갯수"),
                        fieldWithPath("totalPages").description("전체 페이지 갯수"),
                        fieldWithPath("currentPage").description("현재 페이지 번호"),
                        fieldWithPath("pageSize").description("현재 페이지 사이즈"),
                        fieldWithPath("recipes").description("검색 결과 리스트"),
                        fieldWithPath("recipes[].objectId").description("레시피 ID"),
                        fieldWithPath("recipes[].recipeName").description("레시피 이름"),
                        fieldWithPath("recipes[].sourceUrl").description("레시피 Url"),
                        fieldWithPath("recipes[].siteIndex").description("레시피 해당 site 인덱스"),
                        fieldWithPath("recipes[].score").description("해당 레시피 가중치(여기선 사용 X)"),
                        fieldWithPath("recipes[].ingredientList").description("재료 리스트"),
                        fieldWithPath("recipes[].cookingOrderList").description("요리 순서"),
                        fieldWithPath("recipes[].cookingOrderList[].step").description("요리 순서 단계"),
                        fieldWithPath("recipes[].cookingOrderList[].instruction").description("요리 순서 설명")
                )));
    }


}
