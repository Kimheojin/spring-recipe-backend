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

    private static final String SEARCH_INDEX_NAME = "recipe_full_search_kr";
    private static final double DEFAULT_SCORE = 0.0;

    private final MongoTemplate mongoTemplate;

    @Value("${mongo.collectionName}")
    private String collectionName;
    // 페이징 처리 + score 활용해 정렬 + 총 count 추 반환

    // 재료 전용
    @Override
    public SearchRecipeListResponseDto getIngredientResult(int page, int pageSize, String term) {

        AggregationResults<Document> countResults = mongoTemplate.aggregate(
                buildCountAggregation("ingredientList", term),
                collectionName,
                Document.class
        );
        int totalCount = extractTotalCount(countResults);

        // 페이징
        int skip = (page - 1) * pageSize;

        AggregationResults<Recipe> results = mongoTemplate.aggregate(
                buildDataAggregation("ingredientList", term, skip, pageSize),
                collectionName,
                Recipe.class
        );

        List<SearchRecipeResponseDto> recipes = mapRecipes(results);

        return buildResponse(recipes, page, pageSize, totalCount);
    }

    @Override
    public SearchRecipeListResponseDto getRecipeNameResult(int page, int pageSize, String term) {

        AggregationResults<Document> countResults = mongoTemplate.aggregate(
                buildCountAggregation("recipeName", term),
                collectionName,
                Document.class
        );

        int totalCount = extractTotalCount(countResults);

        // 페이징
        int skip = (page - 1) * pageSize;

        AggregationResults<Recipe> results = mongoTemplate.aggregate(
                buildDataAggregation("recipeName", term, skip, pageSize),
                collectionName,
                Recipe.class
        );

        List<SearchRecipeResponseDto> recipes = mapRecipes(results);

        return buildResponse(recipes, page, pageSize, totalCount);

    }

    @Override
    public SearchRecipeListResponseDto getCookingOrderResult(int page, int pageSize, String term) {

        AggregationResults<Document> countResults = mongoTemplate.aggregate(
                buildCountAggregation("cookingOrderList.instruction", term),
                collectionName,
                Document.class
        );

        int totalCount = extractTotalCount(countResults);

        // 페이징
        int skip = (page -1) * pageSize;

        AggregationResults<Recipe> results = mongoTemplate.aggregate(
                buildDataAggregation("cookingOrderList.instruction", term, skip, pageSize),
                collectionName,
                Recipe.class
        );

        List<SearchRecipeResponseDto> recipes = mapRecipes(results);

        return buildResponse(recipes, page, pageSize, totalCount);


    }


    private Aggregation buildCountAggregation(String path, String term) {
        Document textStage = new Document("query", term)
                .append("path", path);
        Document searchMeta = new Document("$searchMeta",
                new Document("index", SEARCH_INDEX_NAME)
                        .append("text", textStage)
                        .append("count", new Document("type", "total")));
        return Aggregation.newAggregation(Aggregation.stage(searchMeta));
    }

    private Aggregation buildDataAggregation(String path, String term, int skip, int limit) {
        Document textStage = new Document("query", term)
                .append("path", path);
        Document searchStage = new Document("$search",
                new Document("index", SEARCH_INDEX_NAME)
                        .append("text", textStage));
        return Aggregation.newAggregation(
                Aggregation.stage(searchStage),
                Aggregation.skip(skip),
                Aggregation.limit(limit)
        );
    }

    private int extractTotalCount(AggregationResults<Document> countResults) {
        if (countResults.getMappedResults().isEmpty()) {
            return 0;
        }
        Document searchMetaDoc = countResults.getMappedResults().get(0);
        Document countDoc = searchMetaDoc.get("count", Document.class);
        if (countDoc == null) {
            return 0;
        }
        Number total = countDoc.get("total", Number.class);
        return total == null ? 0 : total.intValue();
    }

    private List<SearchRecipeResponseDto> mapRecipes(AggregationResults<Recipe> results) {
        return results.getMappedResults().stream()
                .map(recipe -> SearchRecipeResponseDto.from(recipe, DEFAULT_SCORE))
                .collect(Collectors.toList());
    }

    private SearchRecipeListResponseDto buildResponse(List<SearchRecipeResponseDto> recipes,
                                                      int page,
                                                      int pageSize,
                                                      int totalCount) {
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        return SearchRecipeListResponseDto.builder()
                .recipes(recipes)
                .totalCount(totalCount)
                .currentPage(page)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .build();
    }
}
