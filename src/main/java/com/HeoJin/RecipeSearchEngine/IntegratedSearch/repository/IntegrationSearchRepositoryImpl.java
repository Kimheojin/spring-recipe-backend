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

    @Override
    public SearchRecipeListResponseDto getIngredientResult(int page, int pageSize, String term) {
        return performSearch("ingredientList", page, pageSize, term);
    }

    @Override
    public SearchRecipeListResponseDto getRecipeNameResult(int page, int pageSize, String term) {
        return performSearch("recipeName", page, pageSize, term);
    }

    @Override
    public SearchRecipeListResponseDto getCookingOrderResult(int page, int pageSize, String term) {
        return performSearch("cookingOrderList.instruction", page, pageSize, term);
    }

    // 공통 처리 로직
    private SearchRecipeListResponseDto performSearch(String path, int page, int pageSize, String term) {

        // search 연산자랑 같이 해도 되는 데, 비효율적
        // 하이라이트랑 달리 meta 위치가 다름
        Aggregation countAggregation = Aggregation.newAggregation(
                Aggregation.stage(Document.parse("""
                        {
                          "$searchMeta": {
                            "index": "%s",
                            "text": { "query": "%s", "path": "%s" },
                            "count": { "type": "total" }
                          }
                        }
                        """.formatted(SEARCH_INDEX_NAME, term, path)))
        );
        int totalCount = extractTotalCount(mongoTemplate.aggregate(countAggregation, collectionName, Document.class));

        int skip = (page - 1) * pageSize;

        // search 연산
        // 간단
        Aggregation dataAggregation = Aggregation.newAggregation(
                Aggregation.stage(Document.parse("""
                        {
                          "$search": {
                            "index": "%s",
                            "text": { "query": "%s", "path": "%s" }
                          }
                        }
                        """.formatted(SEARCH_INDEX_NAME, term, path))),
                Aggregation.skip(skip),
                Aggregation.limit(pageSize)
        );
        AggregationResults<Recipe> results = mongoTemplate.aggregate(dataAggregation, collectionName, Recipe.class);


        List<SearchRecipeResponseDto> recipes = results.getMappedResults().stream()
                .map(recipe -> SearchRecipeResponseDto.from(recipe, DEFAULT_SCORE))
                .collect(Collectors.toList());


        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        return SearchRecipeListResponseDto.builder()
                .recipes(recipes)
                .totalCount(totalCount)
                .currentPage(page)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .build();
    }

    private int extractTotalCount(AggregationResults<Document> countResults) {

        if (countResults.getMappedResults().isEmpty())
            return 0;

        Document searchMetaDoc = countResults.getMappedResults().get(0);
        Document countDoc = searchMetaDoc.get("count", Document.class);

        if (countDoc == null)
            return 0;
        Number total = countDoc.get("total", Number.class);

        return total == null ? 0 : total.intValue();
    }
}
