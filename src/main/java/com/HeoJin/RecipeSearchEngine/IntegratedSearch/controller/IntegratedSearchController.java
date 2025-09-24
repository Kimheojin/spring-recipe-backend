package com.HeoJin.RecipeSearchEngine.IntegratedSearch.controller;


import com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto.SearchRecipeListResponseDto;
import com.HeoJin.RecipeSearchEngine.IntegratedSearch.service.IntegratedSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seo")
@RequiredArgsConstructor
public class IntegratedSearchController {

    private final IntegratedSearchService integratedSearchService;

    // 나중에 동적으로 통합하기

    // 재료 기반 통합 검색
    @GetMapping("/search/ingredient")
    public ResponseEntity<SearchRecipeListResponseDto> integrationIngredientSearch(
            @RequestParam String term
    ){

        // 이거 페이지 처리 해야하나 (기존 쿼리문 재사용 못하나
        SearchRecipeListResponseDto ingredientSearchData = integratedSearchService.getIngredientSearchData(term);
        return ResponseEntity.ok(ingredientSearchData);
    }

    // cookingOrderlist 기반 검색
    @GetMapping("/search/cookingorder")
    public ResponseEntity<SearchRecipeListResponseDto> integrationCookingOrderSearch(
            @RequestParam String term
    )
    {
        SearchRecipeListResponseDto cookingOrderSearchData = integratedSearchService.getCookingOrderSearchData(term);
        return ResponseEntity.ok(cookingOrderSearchData);
    }

    // 레시피명 기반 통합 검색
    @GetMapping("/search/recipename")
    public ResponseEntity<SearchRecipeListResponseDto> integrationRecipeNameSearch(
            @RequestParam String term
    )
    {
        SearchRecipeListResponseDto recipeNameSearchData = integratedSearchService.getRecipeNameSearchData(term);
        return ResponseEntity.ok(recipeNameSearchData);
    }
    
    // 재료 기반 검색 + 재료 제외 가능하게
    // 필터링 나중에 페이지 작업 완료하면 추가하기

}
