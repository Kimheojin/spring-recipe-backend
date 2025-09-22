package com.HeoJin.RecipeSearchEngine.basicSearch.repository;


import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeResponseDto;

import java.util.List;

public interface RecipeBasicRepository {
    RecipeResponseDto getRecipe( String objectId);

    List<RecipeResponseDto> getFirstPageRecipe(int pageSize);

    // 이전 페이지용
    List<RecipeResponseDto> getLtPageRecipe(int page, int pageSize, String objectId);

    // 다음 페이지 용
    List<RecipeResponseDto> getGtPageRecipe(int page, int pageSize,  String objectId);
}
