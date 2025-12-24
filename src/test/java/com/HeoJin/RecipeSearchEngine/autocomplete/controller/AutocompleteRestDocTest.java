package com.HeoJin.RecipeSearchEngine.autocomplete.controller;


import com.HeoJin.RecipeSearchEngine.autocomplete.dto.AutocompleteIngredientDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.dto.AutocompleteRecipeNameDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.dto.ListAutocompleteIngredientDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.dto.ListAutocompleteRecipeNameDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.service.AutocompleteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
    private MockMvc mockMvc;

    @MockitoBean
    private AutocompleteService autocompleteService;

    @Test
    @DisplayName("ingredient autocomplete restDocs 테스트")
    void test1() throws Exception {
        // given
        String testTerm = "토마토";
        List<AutocompleteIngredientDto> mockResults = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            mockResults.add(new AutocompleteIngredientDto("토마토" + " " +  i, i));
        }

        when(autocompleteService.getIngredientAutocomplete("토마토"))
                .thenReturn(new ListAutocompleteIngredientDto(mockResults));

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

        when(autocompleteService.getRecipeAutocomplete("고구마"))
                .thenReturn(new ListAutocompleteRecipeNameDto(mockResults));

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