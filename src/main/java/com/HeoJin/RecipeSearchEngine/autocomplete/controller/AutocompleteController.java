package com.HeoJin.RecipeSearchEngine.autocomplete.controller;


import com.HeoJin.RecipeSearchEngine.autocomplete.dto.ListAutocompleteIngredientDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.dto.ListAutocompleteRecipeNameDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.service.AutocompleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seo")
@RequiredArgsConstructor
public class AutocompleteController {

    private final AutocompleteService autocompleteService;

    
    // requestParam 으로 받기
    // 재룡전용
    @GetMapping("/autocomplete/ingredient")
    public ResponseEntity<ListAutocompleteIngredientDto> IngredientAutocomplete(
            @RequestParam("term") String term
    ) {
        return ResponseEntity.ok(autocompleteService.getIngredientAutocomplete(term));
    }


    // recipeName 전용
    @GetMapping("/autocomplete/recipename")
    public ResponseEntity<ListAutocompleteRecipeNameDto> recipeNameAutocomplete(
            @RequestParam("term") String term
    ) {
        return ResponseEntity.ok(autocompleteService.getRecipeAutocomplete(term));
    }

}
