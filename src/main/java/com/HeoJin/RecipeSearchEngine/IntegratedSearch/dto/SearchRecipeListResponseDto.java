package com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto;

import java.util.List;

public record SearchRecipeListResponseDto(
        int totalCount,
        int totalPages,
        int pages,
        int pageSize,
        List<SearchRecipeResponseDto> recipes
) {}

