package com.HeoJin.RecipeSearchEngine.basicSearch.repository;


import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeResponseDto;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface RecipeBasicRepository {
    RecipeResponseDto getRecipe(@PathVariable String objectId);

    List<RecipeResponseDto> getFirstPageRecipe(int pageSize);

    // 양수 페이지 증가하는 경우
    List<RecipeResponseDto> getLtPageRecipe(int page, int pageSize, String objectId);

    // 음수 페이지 증가하는 경우
    List<RecipeResponseDto> getGtPageRecipe(int page, int pageSize,  String objectId);
}
