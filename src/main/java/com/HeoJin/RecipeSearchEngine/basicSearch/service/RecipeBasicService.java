package com.HeoJin.RecipeSearchEngine.basicSearch.service;


import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeListResponseDto;
import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeResponseDto;
import com.HeoJin.RecipeSearchEngine.basicSearch.repository.RecipeBasicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeBasicService {
    private final RecipeBasicRepository recipeBasicRepository;


    public RecipeResponseDto getSingleRecipe(String objectId) {
        return recipeBasicRepository.getRecipe(objectId);
    }

    public RecipeListResponseDto getPageRecipe(String objectId, int page, int pageSize) {
        if(objectId == ""){ // 공백인 경우 첫 페이지 반환
            new RecipeListResponseDto(recipeBasicRepository.getFirstPageRecipe(pageSize));
        }
        // 아닌 경우 object id 기반으로 넘긴 다음에 하기
        
        return new RecipeListResponseDto(recipeBasicRepository.getPageRecipe(objectId, page, pageSize));
    }


}

