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

    public ListSearchIngredientDto getIngredientSearchData(String term) {

        List<SearchIngredientDto> ingredientResult = integrationSearchRepository.getIngredientResult(term);

        return new ListSearchIngredientDto(ingredientResult);
    }

    public ListSearchCookingOrderDto getCookingOrderSearchData(String term) {
        List<SearchCookingOrderDto> cookingOrderResult = integrationSearchRepository.getCookingOrderResult(term);
        return new ListSearchCookingOrderDto(cookingOrderResult);
    }

    public ListSearchRecipeNameDto getRecipeNameSearchData(String term) {
        List<SearchRecipeNameDto> recipeNameResult = integrationSearchRepository.getRecipeNameResult(term);
        return new ListSearchRecipeNameDto(recipeNameResult);
    }
}
