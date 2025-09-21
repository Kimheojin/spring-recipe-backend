package com.HeoJin.RecipeSearchEngine.global.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class cookingOrder {
    private int step;
    private String instruction;
}
