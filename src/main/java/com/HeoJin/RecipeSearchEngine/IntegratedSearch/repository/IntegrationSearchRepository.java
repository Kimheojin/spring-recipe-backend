package com.HeoJin.RecipeSearchEngine.IntegratedSearch.repository;

import com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto.SearchCookingOrderDto;
import com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto.SearchIngredientDto;
import com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto.SearchRecipeNameDto;

import java.util.List;

public interface IntegrationSearchRepository {
    List<SearchIngredientDto> getIngredientResult(String term);
    List<SearchRecipeNameDto> getRecipeNameResult(String term);
    List<SearchCookingOrderDto> getCookingOrderResult(String term);
}
