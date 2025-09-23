package com.HeoJin.RecipeSearchEngine.IntegratedSearch.repository;


import com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto.SearchCookingOrderDto;
import com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto.SearchIngredientDto;
import com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto.SearchRecipeNameDto;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("!test")
public class IntegrationSearchRepositoryImpl implements IntegrationSearchRepository {

    // 재료 전용
    @Override
    public List<SearchIngredientDto> getIngredientResult(String term) {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.stage(Document.parse("""
                        {
                            "$search": {
                                "index": "recipe_full_search_kr",
                                "string": {
                                    "query": 
                                }
                        }"""))
        );
        return List.of();
    }

    @Override
    public List<SearchRecipeNameDto> getRecipeNameResult(String term) {
        return List.of();
    }

    @Override
    public List<SearchCookingOrderDto> getCookingOrderResult(String term) {
        return List.of();
    }
    // count 값도 같이 반환하기
        //

}
