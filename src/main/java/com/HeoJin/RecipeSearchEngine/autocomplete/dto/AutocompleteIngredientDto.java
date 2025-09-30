package com.HeoJin.RecipeSearchEngine.autocomplete.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutocompleteIngredientDto {
    private String ingredient;
    private double score; // 높은게 좋음
}
