package com.HeoJin.RecipeSearchEngine.guest.dto.response;

import java.util.List;

public record RecipeStatusListResponseDto(
        List<RecipeStatusDto> recipeStatuses
) {}
