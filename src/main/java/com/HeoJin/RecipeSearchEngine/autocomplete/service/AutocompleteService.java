package com.HeoJin.RecipeSearchEngine.autocomplete.service;


import com.HeoJin.RecipeSearchEngine.autocomplete.dto.ListAutocompleteDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.repository.AutocompleteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutocompleteService {

    private final AutocompleteRepository autocompleteRepository;
    public ListAutocompleteDto getIngredientAutocomplete(String term) {
        return null;

    }

    public ListAutocompleteDto getRecipeAutocomplete(String term) {
        return null;
    }
}
