package com.HeoJin.RecipeSearchEngine.IntegratedSearch.service;


import com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto.*;
import com.HeoJin.RecipeSearchEngine.IntegratedSearch.repository.IntegrationSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IntegratedSearchService {

    private final IntegrationSearchRepository integrationSearchRepository;

    public SearchRecipeListResponseDto getIngredientSearchData(String term) {

        List<SearchRecipeResponseDto> ingredientResult = integrationSearchRepository.getIngredientResult(term);

        return new SearchRecipeListResponseDto(ingredientResult);
    }

    public SearchRecipeListResponseDto getCookingOrderSearchData(String term) {
        List<SearchRecipeResponseDto> cookingOrderResult = integrationSearchRepository.getCookingOrderResult(term);
        return new SearchRecipeListResponseDto(cookingOrderResult);
    }

    public SearchRecipeListResponseDto getRecipeNameSearchData(String term) {
        List<SearchRecipeResponseDto> recipeNameResult = integrationSearchRepository.getRecipeNameResult(term);
        return new SearchRecipeListResponseDto(recipeNameResult);
    }
}
