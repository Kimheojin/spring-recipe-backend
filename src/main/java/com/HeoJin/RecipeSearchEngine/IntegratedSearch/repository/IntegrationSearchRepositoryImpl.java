package com.HeoJin.RecipeSearchEngine.IntegratedSearch.repository;


import com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto.SearchRecipeResponseDto;
import com.HeoJin.RecipeSearchEngine.global.entity.Recipe;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Profile("!test")
@RequiredArgsConstructor
public class IntegrationSearchRepositoryImpl implements IntegrationSearchRepository {

    private final MongoTemplate mongoTemplate;

    @Value("${mongo.collectionName}")
    private String collectionName;

    // 페이징 나중에 생각하기
    // 재료 전용
    @Override
    public List<SearchRecipeResponseDto> getIngredientResult(String term) {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.stage(Document.parse("""
                        {
                            "$search": {
                                "index": "recipe_full_search_kr",
                                "text": {
                                    "query": "%s",
                                    "path": "ingredientList"
                                }
                            }
                        }""".formatted(term))),
                Aggregation.limit(10));
        AggregationResults<Recipe> results = mongoTemplate.aggregate(
                aggregation,
                collectionName,
                Recipe.class
        );

        return results.getMappedResults().stream()
                .map(SearchRecipeResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<SearchRecipeResponseDto> getRecipeNameResult(String term) {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.stage(Document.parse("""
                {
                    "$search": {
                        "index": "recipe_full_search_kr",
                        "text": {
                            "query": "%s",
                            "path": "recipeName"
                        }
                    }
                }""".formatted(term))),
                Aggregation.limit(10));
        AggregationResults<Recipe> results = mongoTemplate.aggregate(
                aggregation,
                collectionName,
                Recipe.class
        );
        return results.getMappedResults().stream()
                .map(SearchRecipeResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<SearchRecipeResponseDto> getCookingOrderResult(String term) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.stage(Document.parse("""
                        {
                            "$search": {
                                "index": "recipe_full_search_kr",
                                "text": {
                                    "query": "%s",
                                    "path": "cookingOrderList.instruction"
                                }
                            }
                        }""".formatted(term))),
                Aggregation.limit(10));
        AggregationResults<Recipe> results = mongoTemplate.aggregate(
                aggregation,
                collectionName,
                Recipe.class
        );
        return results.getMappedResults().stream()
                .map(SearchRecipeResponseDto::from)
                .collect(Collectors.toList());
    }
    // count 값도 같이 반환하기
        //

}
