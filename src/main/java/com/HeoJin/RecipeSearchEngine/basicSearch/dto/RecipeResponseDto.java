package com.HeoJin.RecipeSearchEngine.basicSearch.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class RecipeResponseDto {

    private String recipeName;
    private String sourceUrl;
    private String siteIndex;

    @Builder.Default
    private List<CookingOrderDto> cookingOrder = new ArrayList<>();
    @Builder.Default
    private List<String> ingredients = new ArrayList<>();


}
