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

    public SearchRecipeListResponseDto getIngredientSearchData(int page, int pageSize, String term) {

        SearchRecipeListResponseDto ingredientResult = integrationSearchRepository
                .getIngredientResult(page, pageSize, term);

        return ingredientResult;
    }

    public SearchRecipeListResponseDto getCookingOrderSearchData(int page, int pageSize, String term) {
        SearchRecipeListResponseDto cookingOrderResult = integrationSearchRepository
                .getCookingOrderResult(page, pageSize, term);
        return cookingOrderResult;
    }

    public SearchRecipeListResponseDto getRecipeNameSearchData(int page, int pageSize, String term) {
        SearchRecipeListResponseDto recipeNameResult = integrationSearchRepository
                .getRecipeNameResult(page, pageSize, term);
        return recipeNameResult;
    }
}
