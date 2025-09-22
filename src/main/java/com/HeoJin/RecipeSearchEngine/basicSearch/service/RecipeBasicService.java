package com.HeoJin.RecipeSearchEngine.basicSearch.service;


import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeListResponseDto;
import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeResponseDto;
import com.HeoJin.RecipeSearchEngine.basicSearch.repository.RecipeBasicRepository;
import com.HeoJin.RecipeSearchEngine.global.exception.mongo.CustomMongoNullException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeBasicService {
    private final RecipeBasicRepository recipeBasicRepository;


    public RecipeResponseDto getSingleRecipe(String objectId) {
        RecipeResponseDto recipe = recipeBasicRepository.getRecipe(objectId);

        if(recipe == null){
            throw new CustomMongoNullException("단일 레시피 조회");
        }
        return recipe;


    }

    public RecipeListResponseDto getPageRecipe(int page, int pageSize, String objectId) {
        if (page > 0) {
            return new RecipeListResponseDto(recipeBasicRepository.getGtPageRecipe(page, pageSize, objectId));
        } else if (page < 0) {
            return new RecipeListResponseDto(recipeBasicRepository.getLtPageRecipe(page, pageSize, objectId));
        } else {
            // page == 0인 경우 처리
            return new RecipeListResponseDto(recipeBasicRepository.getFirstPageRecipe(pageSize));
        }

    }


}

