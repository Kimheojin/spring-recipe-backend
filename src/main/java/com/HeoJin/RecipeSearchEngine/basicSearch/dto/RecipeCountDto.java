package com.HeoJin.RecipeSearchEngine.basicSearch.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeCountDto {
    private long recipeCount;
}
