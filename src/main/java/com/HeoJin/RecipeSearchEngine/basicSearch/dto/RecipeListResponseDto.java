package com.HeoJin.RecipeSearchEngine.basicSearch.dto;

import java.util.List;

public record RecipeListResponseDto(
        List<RecipeResponseDto> recipes
) {}

