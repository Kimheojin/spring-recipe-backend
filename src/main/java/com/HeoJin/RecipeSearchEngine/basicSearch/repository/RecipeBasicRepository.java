package com.HeoJin.RecipeSearchEngine.basicSearch.repository;


import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeResponseDto;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface RecipeBasicRepository {
    RecipeResponseDto getRecipe(@PathVariable String objectId);

    List<RecipeResponseDto> getFirstPageRecipe(int pageSize);

    List<RecipeResponseDto> getPageRecipe(String objectId, int page, int pageSize);


}
