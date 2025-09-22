package com.HeoJin.RecipeSearchEngine.autocomplete.service;


import com.HeoJin.RecipeSearchEngine.autocomplete.dto.AutocompleteIngredientDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.dto.AutocompleteRecipeNameDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.dto.ListAutocompleteIngredientDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.dto.ListAutocompleteRecipeNameDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.repository.AutocompleteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutocompleteService {

    private final AutocompleteRepository autocompleteRepository;
    public ListAutocompleteIngredientDto getIngredientAutocomplete(String term) {

        List<AutocompleteIngredientDto> resultAboutIngredient = autocompleteRepository.getResultAboutIngredient(term);
        return new ListAutocompleteIngredientDto(resultAboutIngredient);

    }

    public ListAutocompleteRecipeNameDto getRecipeAutocomplete(String term) {

        List<AutocompleteRecipeNameDto> resultAboutRecipeName = autocompleteRepository.getResultAboutRecipeName(term);
        return new ListAutocompleteRecipeNameDto(resultAboutRecipeName);
    }
}
