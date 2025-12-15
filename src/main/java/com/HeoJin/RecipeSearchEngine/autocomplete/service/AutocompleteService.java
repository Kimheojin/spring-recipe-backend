package com.HeoJin.RecipeSearchEngine.autocomplete.service;


import com.HeoJin.RecipeSearchEngine.autocomplete.dto.AutocompleteIngredientDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.dto.AutocompleteRecipeNameDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.dto.ListAutocompleteIngredientDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.dto.ListAutocompleteRecipeNameDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.repository.AutocompleteRepository;
import com.HeoJin.RecipeSearchEngine.global.exception.BusinessException;
import com.HeoJin.RecipeSearchEngine.global.exception.ErrorCode.EnumErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutocompleteService {

    private final AutocompleteRepository autocompleteRepository;

    @Cacheable(cacheNames = "autocomplete:ingredient", key = "#term")
    public ListAutocompleteIngredientDto getIngredientAutocomplete(String term) {
        validateTerm(term);
        List<AutocompleteIngredientDto> resultAboutIngredient = autocompleteRepository.getResultAboutIngredient(term);
        return new ListAutocompleteIngredientDto(resultAboutIngredient);

    }
    @Cacheable(cacheNames = "autocomplete:recipeName", key = "#term")
    public ListAutocompleteRecipeNameDto getRecipeAutocomplete(String term) {
        validateTerm(term);
        List<AutocompleteRecipeNameDto> resultAboutRecipeName = autocompleteRepository.getResultAboutRecipeName(term);
        return new ListAutocompleteRecipeNameDto(resultAboutRecipeName);
    }

    private void validateTerm(String term) {
        if (term == null || term.trim().isEmpty()) {
            throw new BusinessException(EnumErrorCode.INVALID_REQUEST, "검색어를 입력해주세요.");
        }
    }
}
