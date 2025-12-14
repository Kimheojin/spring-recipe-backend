package com.HeoJin.RecipeSearchEngine.IntegratedSearch.service;


import com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto.SearchRecipeListResponseDto;
import com.HeoJin.RecipeSearchEngine.IntegratedSearch.repository.IntegrationSearchRepository;
import com.HeoJin.RecipeSearchEngine.global.exception.BusinessException;
import com.HeoJin.RecipeSearchEngine.global.exception.ErrorCode.EnumErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IntegratedSearchService {

    private final IntegrationSearchRepository integrationSearchRepository;

    public SearchRecipeListResponseDto getIngredientSearchData(int page, int pageSize, String term) {
        validateTerm(term);
        SearchRecipeListResponseDto ingredientResult = integrationSearchRepository
                .getIngredientResult(page, pageSize, term);

        return ingredientResult;
    }

    public SearchRecipeListResponseDto getCookingOrderSearchData(int page, int pageSize, String term) {
        validateTerm(term);
        SearchRecipeListResponseDto cookingOrderResult = integrationSearchRepository
                .getCookingOrderResult(page, pageSize, term);
        return cookingOrderResult;
    }

    public SearchRecipeListResponseDto getRecipeNameSearchData(int page, int pageSize, String term) {
        validateTerm(term);
        SearchRecipeListResponseDto recipeNameResult = integrationSearchRepository
                .getRecipeNameResult(page, pageSize, term);
        return recipeNameResult;
    }

    private void validateTerm(String term) {
        if (term == null || term.trim().isEmpty()) {
            throw new BusinessException(EnumErrorCode.INVALID_REQUEST, "검색어를 입력해주세요.");
        }
    }
}
