package com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto;


import com.HeoJin.RecipeSearchEngine.global.entity.Recipe;
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
public class SearchRecipeResponseDto {

    private String objectId;
    private String recipeName;
    private String sourceUrl;
    private String siteIndex;

    @Builder.Default
    private List<SearchCookingOrderDto> cookingOrderList = new ArrayList<>();
    @Builder.Default
    private List<String> ingredientList = new ArrayList<>();

    public static SearchRecipeResponseDto from(Recipe recipe) {
        return SearchRecipeResponseDto.builder()
                .objectId(recipe.getId())
                .recipeName(recipe.getRecipeName())
                .cookingOrderList(recipe.getCookingOrderList().stream()
                        .map(order -> SearchCookingOrderDto.builder()
                                .step(order.getStep())
                                .instruction(order.getInstruction())
                                .build())
                        .toList())
                .sourceUrl(recipe.getSourceUrl())
                .siteIndex(recipe.getSiteIndex())
                .ingredientList(recipe.getIngredientList())
                .build();
    }
}
