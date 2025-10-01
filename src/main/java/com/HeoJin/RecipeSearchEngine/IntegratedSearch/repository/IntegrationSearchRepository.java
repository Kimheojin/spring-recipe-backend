package com.HeoJin.RecipeSearchEngine.IntegratedSearch.repository;

import com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto.SearchRecipeListResponseDto;

public interface IntegrationSearchRepository {
    SearchRecipeListResponseDto getIngredientResult(int page, int pageSize, String term);
    SearchRecipeListResponseDto getRecipeNameResult(int page, int pageSize, String term);
    SearchRecipeListResponseDto getCookingOrderResult(int page, int pageSize, String term);
}
