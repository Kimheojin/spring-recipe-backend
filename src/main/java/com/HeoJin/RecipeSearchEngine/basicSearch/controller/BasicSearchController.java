package com.HeoJin.RecipeSearchEngine.basicSearch.controller;


import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeCountDto;
import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeListResponseDto;
import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeResponseDto;
import com.HeoJin.RecipeSearchEngine.basicSearch.service.RecipeBasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seo")
@RequiredArgsConstructor
public class BasicSearchController {

    private final RecipeBasicService recipeBasicService;
    // 단일 조회 먼저
    @GetMapping("/basic/recipe")
    public ResponseEntity<RecipeResponseDto> getRecipe(
            @RequestParam String objectId
    ) {
        RecipeResponseDto singleRecipe = recipeBasicService.getSingleRecipe(objectId);
        return ResponseEntity.ok(singleRecipe);
    }


    // 전체 조회 + id 기반  페이징 지원
    // 인덱스 태우고 페이지 객체 따로 만들어야 할듯
    @GetMapping("/basic/recipes")
    public ResponseEntity<RecipeListResponseDto> getAllRecipe(
            @RequestParam(defaultValue = "0") int page, // 현재 page 와 페이지 수 차이
            @RequestParam(defaultValue = "10") int pageSize, // 페이지 사이즈
            @RequestParam(defaultValue = "") String objectId // 기준
    ) {
        RecipeListResponseDto pageRecipe = recipeBasicService.getPageRecipe(page, pageSize, objectId);
        return ResponseEntity.ok(pageRecipe);
    }

    // 전체 데이터 갯수 확인용 엔드 포인트
    @GetMapping("/basic/recipescount")
    public ResponseEntity<RecipeCountDto> getCount(){
        RecipeCountDto recipeCount = recipeBasicService.getRecipeCount();
        return ResponseEntity.ok(recipeCount);
    }


}
