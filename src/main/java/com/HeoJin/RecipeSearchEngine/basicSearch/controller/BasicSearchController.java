package com.HeoJin.RecipeSearchEngine.basicSearch.controller;


import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeListResponseDto;
import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeResponseDto;
import com.HeoJin.RecipeSearchEngine.basicSearch.service.RecipeBasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seo")
@RequiredArgsConstructor
public class BasicSearchController {

    private final RecipeBasicService recipeBasicService;
    // 단일 조회 먼저
    @GetMapping("/basic/recipe/{objectId}")
    public ResponseEntity<RecipeResponseDto> getRecipe(
            @PathVariable String objectId
    ) {
        return ResponseEntity.ok(recipeBasicService.getSingleRecipe(objectId));
    }


    // 전체 조회 + id 기반  페이징 지원
    // 인덱스 태우고 페이지 객체 따로 만들어야 할듯
    @GetMapping("/basic/recipes/{pageNumber}/{pageSize}/{objectId}")
    public ResponseEntity<RecipeListResponseDto> getAllRecipe(
            @PathVariable int pageNumber, // 현재 page 와 페이지 수 차이
            @PathVariable int pageSize, // 페이지 사이즈
            @PathVariable String objectId // 기준
    ) {
        return ResponseEntity.ok(recipeBasicService.getPageRecipe(objectId, pageNumber, pageSize));
    }


}
