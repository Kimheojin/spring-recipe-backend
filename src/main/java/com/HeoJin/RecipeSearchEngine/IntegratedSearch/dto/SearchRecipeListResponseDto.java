package com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record SearchRecipeListResponseDto(
        int totalCount,
        int totalPages,
        int currentPage,
        int pageSize,
        List<SearchRecipeResponseDto> recipes
) {}

