package com.HeoJin.RecipeSearchEngine.basicSearch.dto;


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
public class RecipeResponseDto {

    private String recipeName;
    private String sourceUrl;
    private String siteIndex;

    @Builder.Default
    private List<CookingOrderDto> cookingOrder = new ArrayList<>();
    @Builder.Default
    private List<String> ingredients = new ArrayList<>();

    public static RecipeResponseDto from(Recipe recipe) {
        return RecipeResponseDto.builder()
                .recipeName(recipe.getRecipeName())
                .cookingOrder(recipe.getCookingOrderList().stream()
                        .map(order -> CookingOrderDto.builder()
                                .step(order.getStep())
                                .instruction(order.getInstruction())
                                .build())
                        .toList())
                .sourceUrl(recipe.getSourceUrl())
                .siteIndex(recipe.getSiteIndex())
                .ingredients(recipe.getIngredients())
                .build();
    }
}
