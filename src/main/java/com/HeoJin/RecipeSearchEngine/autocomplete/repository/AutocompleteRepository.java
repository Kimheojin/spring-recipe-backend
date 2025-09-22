package com.HeoJin.RecipeSearchEngine.autocomplete.repository;

import com.HeoJin.RecipeSearchEngine.autocomplete.dto.AutocompleteIngredientDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.dto.AutocompleteRecipeNameDto;

import java.util.List;

public interface AutocompleteRepository {

    // 재료 기반
    List<AutocompleteIngredientDto> getResultAboutIngredient(String term);
    // recipeName 기반
    List<AutocompleteRecipeNameDto> getResultAboutRecipeName(String term);

}
