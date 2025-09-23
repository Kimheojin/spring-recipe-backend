package com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto;

import java.util.List;

public record SearchRecipeListResponseDto(
        List<SearchRecipeResponseDto> recipes
) {}

