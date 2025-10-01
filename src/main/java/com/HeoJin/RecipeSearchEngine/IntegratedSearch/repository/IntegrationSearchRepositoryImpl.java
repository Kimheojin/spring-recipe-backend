package com.HeoJin.RecipeSearchEngine.IntegratedSearch.repository;


import com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto.SearchRecipeListResponseDto;
import com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto.SearchRecipeResponseDto;
import com.HeoJin.RecipeSearchEngine.global.entity.Recipe;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class IntegrationSearchRepositoryImpl implements IntegrationSearchRepository {

    private final MongoTemplate mongoTemplate;

    @Value("${mongo.collectionName}")
    private String collectionName;
    // 페이징 처리 + score 활용해 정렬 + 총 count 추 반환

    // 페이징 나중에 생각하기
    // 재료 전용
    @Override
    public SearchRecipeListResponseDto getIngredientResult(int page, int pageSize, String term) {

        // 전체 데이터 갯수
        Aggregation countAggregation = Aggregation.newAggregation(
                Aggregation.stage(Document.parse("""
                {
                    "$searchMeta": {
                        "index": "recipe_full_search_kr",
                        "text": {
                            "query": "%s",
                            "path": "ingredientList"
                        },
                        "count": {
                            "type": "total"
                        }
                    }
                }""".formatted(term)))
        );

        AggregationResults<Document> countResults = mongoTemplate.aggregate(
                countAggregation,
                collectionName,
                Document.class
        );

        int totalCount = 0;
        if (!countResults.getMappedResults().isEmpty()) {
            Document countDoc = countResults.getMappedResults().get(0);
            totalCount = countDoc.get("count", Document.class).getInteger("total");
        }

        // 페이징
        int skip = (page - 1) * pageSize;

        Aggregation dataAggregation = Aggregation.newAggregation(
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
                Aggregation.skip(skip),
                Aggregation.limit(pageSize)
        );

        AggregationResults<Recipe> results = mongoTemplate.aggregate(
                dataAggregation,
                collectionName,
                Recipe.class
        );

        List<SearchRecipeResponseDto> recipes = results.getMappedResults().stream()
                .map(recipe -> SearchRecipeResponseDto.from(recipe, 0.0))
                .collect(Collectors.toList());

        return SearchRecipeListResponseDto.builder()
                .recipes(recipes)
                .totalCount(totalCount)
                .currentPage(page)
                .pageSize(pageSize)
                .totalPages((int) Math.ceil((double) totalCount / pageSize)) // 올림
                .build();
    }

    @Override
    public SearchRecipeListResponseDto getRecipeNameResult(int page, int pageSize, String term) {

        // 전체 데이터 갯수
        Aggregation countAggregation = Aggregation.newAggregation(
                Aggregation.stage(Document.parse("""
                    {
                        "$searchMeta": {
                            "index": "recipe_full_search_kr",
                            "text": {
                                "query": "%s",
                                "path": "recipeName"
                            },
                            "count": {
                                "type": "total"
                            }
                        }
                    }""".formatted(term))));
        AggregationResults<Document> countResults = mongoTemplate.aggregate(
                countAggregation,
                collectionName,
                Document.class
        );

        int totalCount = 0;
        if (!countResults.getMappedResults().isEmpty()) {
            Document countDoc = countResults.getMappedResults().get(0);
            totalCount = countDoc.get("count", Document.class).getInteger("total");
        }

        // 페이징
        int skip = (page - 1) * pageSize;

        Aggregation dataAggregation = Aggregation.newAggregation(
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
                Aggregation.skip(skip),
                Aggregation.limit(pageSize)
        );

        AggregationResults<Recipe> results = mongoTemplate.aggregate(
                dataAggregation,
                collectionName,
                Recipe.class
        );

        List<SearchRecipeResponseDto> recipes = results.getMappedResults().stream()
                .map(recipe -> SearchRecipeResponseDto.from(recipe, 0.0))
                .collect(Collectors.toUnmodifiableList());

        return SearchRecipeListResponseDto.builder()
                .recipes(recipes)
                .totalCount(totalCount)
                .currentPage(page)
                .pageSize(pageSize)
                .totalPages((int) Math.ceil((double) totalCount/pageSize))
                .build();

    }

    @Override
    public SearchRecipeListResponseDto getCookingOrderResult(int page, int pageSize, String term) {

        Aggregation countAggregation = Aggregation.newAggregation(
                Aggregation.stage(Document.parse("""
                    {
                        "$searchMeta": {
                            "index": "recipe_full_search_kr",
                            "text": {
                                "query": "%s",
                                "path": "cookingOrderList.instruction"
                            },
                            "count": {
                                "type": "total"
                            }
                        }
                    }""".formatted(term))));

        AggregationResults<Document> countResults = mongoTemplate.aggregate(
                countAggregation,
                collectionName,
                Document.class
        );

        int totalCount = 0;
        if (!countResults.getMappedResults().isEmpty()) {
            Document countDoc = countResults.getMappedResults().get(0);
            totalCount = countDoc.get("count", Document.class).getInteger("total");
        }

        // 페이징
        int skip = (page -1) * pageSize;

        Aggregation dataAggregation = Aggregation.newAggregation(
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
                Aggregation.skip(skip),
                Aggregation.limit(pageSize)
        );

        AggregationResults<Recipe> results = mongoTemplate.aggregate(
                dataAggregation,
                collectionName,
                Recipe.class
        );

        List<SearchRecipeResponseDto> recipes = results.getMappedResults().stream()
                .map(recipe-> SearchRecipeResponseDto.from(recipe, 0.0))
                .collect(Collectors.toList());

        return SearchRecipeListResponseDto.builder()
                .recipes(recipes)
                .totalCount(totalCount)
                .currentPage(page)
                .pageSize(pageSize)
                .totalPages((int) Math.ceil((double) totalCount / pageSize)) // 올림
                .build();


    }


}
