package com.HeoJin.RecipeSearchEngine.guest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Builder
public class RecipeStatusDto {

    private String recipeId;
    private boolean liked;
    private boolean bookmarked;

}
