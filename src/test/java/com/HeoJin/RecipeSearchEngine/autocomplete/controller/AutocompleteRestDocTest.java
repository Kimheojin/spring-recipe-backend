package com.HeoJin.RecipeSearchEngine.autocomplete.controller;


import com.HeoJin.RecipeSearchEngine.Init.TestService;
import com.HeoJin.RecipeSearchEngine.autocomplete.dto.AutocompleteIngredientDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.dto.AutocompleteRecipeNameDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.repository.AutocompleteRepository;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class AutocompleteRestDocTest {


    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private TestService testService;
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutocompleteRepository autocompleteRepository;

    @Value("${mongo.collectionName}")
    private String collectionName;



    @BeforeEach
    void init() {
        // 데이터 없는 경우에만 삽입
        // 어처피 select 로직 밖에 없을듯
        if (mongoTemplate.count(new Query(), collectionName) == 0) {
            testService.insertInitData();
        }
    }

    @Test
    @DisplayName("ingredient autocomplete restDocs 테스트")
    void test1() throws Exception {
        // given
        String testTerm = "토마토";
        // atlas search 인덱스 사용해서 test 못할듯, mock 으로 하기
        List<AutocompleteIngredientDto> mockResults = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            mockResults.add(new AutocompleteIngredientDto("토마토" + " " +  i, i));
        }

        when(autocompleteRepository.getResultAboutIngredient("토마토"))
                .thenReturn(mockResults);

        // when + then
        ResultActions testMock = mockMvc.perform(get("/seo/autocomplete/ingredient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("term", testTerm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autocompleteDtoList").isArray())
                .andExpect(jsonPath("$.autocompleteDtoList.length()").value(mockResults.size()))
                .andExpect(jsonPath("$.autocompleteDtoList[0].ingredient").value(mockResults.get(0).getIngredient()))
                .andDo(print());

        // Docs

        testMock.andDo(document("get-/seo/autocomplete/ingredient autocomplete ingredient test",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("term").description("자동완성 하고자 하는 단어")
                ),
                responseFields(
                    fieldWithPath("autocompleteDtoList").description("자동완성 결과 리스트"),
                    fieldWithPath("autocompleteDtoList[].ingredient").description("자동완성된 재료명"),
                    fieldWithPath("autocompleteDtoList[].score").description("score 값")
                )));
    }
    @Test
    @DisplayName("recipeName autocomplete restDocs 테스트")
    void test2() throws Exception {
        // given
        String testTerm = "고구마";

        List<AutocompleteRecipeNameDto> mockResults = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            mockResults.add(new AutocompleteRecipeNameDto("고구마맛 토마토 + " + i, i));
        }

        when(autocompleteRepository.getResultAboutRecipeName("고구마"))
                .thenReturn(mockResults);

        // when + then
        ResultActions testMock = mockMvc.perform(get("/seo/autocomplete/recipename")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("term", testTerm))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autocompleteRecipeNameDtoList").isArray())
                .andExpect(jsonPath("$.autocompleteRecipeNameDtoList.length()").value(mockResults.size()))
                .andExpect(jsonPath("$.autocompleteRecipeNameDtoList[0].recipeName").value(mockResults.get(0).getRecipeName()))
                .andDo(print());

        // Docs
        testMock.andDo(document("get-/seo/autocomplete/recipename autocomplete recipename test",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                queryParameters(
                        parameterWithName("term").description("자동완성 하고자 하는 단어")
                ),
                responseFields(
                        fieldWithPath("autocompleteRecipeNameDtoList").description("자동완성 결과 리스트"),
                        fieldWithPath("autocompleteRecipeNameDtoList[].recipeName").description("자동완성된 레시피 이름"),
                        fieldWithPath("autocompleteRecipeNameDtoList[].score").description("score 값")
                )));
    }

}
