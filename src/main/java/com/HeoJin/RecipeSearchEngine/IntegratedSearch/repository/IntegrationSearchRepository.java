package com.HeoJin.RecipeSearchEngine.IntegratedSearch.repository;

import com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto.SearchRecipeResponseDto;

import java.util.List;

public interface IntegrationSearchRepository {
    List<SearchRecipeResponseDto> getIngredientResult(String term);
    List<SearchRecipeResponseDto> getRecipeNameResult(String term);
    List<SearchRecipeResponseDto> getCookingOrderResult(String term);
}
