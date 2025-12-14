package com.HeoJin.RecipeSearchEngine.guest.dto.response;

import java.util.List;

public record RecipeBookmarkListResponseDto(
        List<String> recipeIds
) {}
