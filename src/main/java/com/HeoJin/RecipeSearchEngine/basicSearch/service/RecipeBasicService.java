package com.HeoJin.RecipeSearchEngine.basicSearch.service;


import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeCountDto;
import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeListResponseDto;
import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeResponseDto;
import com.HeoJin.RecipeSearchEngine.basicSearch.repository.RecipeBasicRepository;
import com.HeoJin.RecipeSearchEngine.global.exception.BusinessException;
import com.HeoJin.RecipeSearchEngine.global.exception.ErrorCode.EnumErrorCode;
import com.HeoJin.RecipeSearchEngine.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeBasicService {
    private final RecipeBasicRepository recipeBasicRepository;


    public RecipeResponseDto getSingleRecipe(String objectId) {
        validateObjectId(objectId);
        RecipeResponseDto recipe = recipeBasicRepository.getRecipe(objectId);

        if(recipe == null){
            throw new NotFoundException("레시피가 존재하지 않습니다.");
        }
        return recipe;


    }

    public RecipeListResponseDto getPageRecipe(int page, int pageSize, String objectId) {
        if (page > 0) {
            validateObjectId(objectId);
            return new RecipeListResponseDto(recipeBasicRepository.getGtPageRecipe(page, pageSize, objectId));
        } else if (page < 0) {
            validateObjectId(objectId);
            return new RecipeListResponseDto(recipeBasicRepository.getLtPageRecipe(page, pageSize, objectId));
        } else {
            // page == 0인 경우 처리
            return new RecipeListResponseDto(recipeBasicRepository.getFirstPageRecipe(pageSize));
        }

    }


    public RecipeCountDto getRecipeCount() {

        return recipeBasicRepository.getRecipeCount();
    }

    private void validateObjectId(String objectId) {
        if (objectId == null || objectId.trim().isEmpty()) {
            throw new BusinessException(EnumErrorCode.INVALID_REQUEST, "잘못된 레시피 ID입니다.");
        }
    }
}

