package com.HeoJin.RecipeSearchEngine.IntegratedSearch.controller;


import com.HeoJin.RecipeSearchEngine.IntegratedSearch.service.IntegratedSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seo")
@RequiredArgsConstructor
public class IntegratedSearchController {

    private final IntegratedSearchService integratedSearchService;

    // 나중에 동적으로 통합하기

    // 재료 기반 통합 검색
    @GetMapping("/ingredient")
    public ResponseEntity<Object> integrationIngredientSearch(){
        return null;
    }
    
    // cookingOrderlist 기반 검색
    @GetMapping("/cookingorder")
    public ResponseEntity<Object> integrationCookingOrderSearch()
    {
        return null;
    }

    // 레시피명 기반 통합 검색
    @GetMapping("/recipename")
    public ResponseEntity<Object> integrationRecipeNameSearch()
    {
        return null;
    }
    
    // 재료 기반 검색 + 재료 제외 가능하게
    @GetMapping("/filter/ingredient")
    public ResponseEntity<Object> integrationIngredientWithFilterSearch()
    {
        return null;
    }




}
