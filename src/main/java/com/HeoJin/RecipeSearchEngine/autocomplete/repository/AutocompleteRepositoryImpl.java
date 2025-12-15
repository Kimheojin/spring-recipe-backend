package com.HeoJin.RecipeSearchEngine.autocomplete.repository;


import com.HeoJin.RecipeSearchEngine.autocomplete.dto.AutocompleteIngredientDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.dto.AutocompleteRecipeNameDto;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
public class AutocompleteRepositoryImpl implements AutocompleteRepository {

    private final MongoTemplate mongoTemplate;

    @Value("${mongo.collectionName}")
    private String collectionName;

    // 재료명 기반 자동 완성
    @Override
    public List<AutocompleteIngredientDto> getResultAboutIngredient(String term) {

        // 인덱스 지정 json
        Document ingredientSearchStage = new Document("$search",
                new Document("index", "autocomplete_kr")
                        .append("autocomplete", new Document("query", term)
                                .append("path", "ingredientList")
                                .append("tokenOrder", "any")));

        // score json 필드 추가
        Document addFieldsStage = new Document("$addFields",
                new Document("score", new Document("$meta", "searchScore")));

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.stage(ingredientSearchStage),
                Aggregation.stage(addFieldsStage),
                // ingredientList 중 term 으로 시작하는 값만 필터링
                Aggregation.project()
                        .andExpression(filterIngredientExpression(term))
                        .as("matchingIngredients")
                        .and("score").as("score"),
                // 재료 배열 펼치기
                Aggregation.unwind("matchingIngredients"),
                Aggregation.project()
                        .andExpression("$matchingIngredients")
                        .as("ingredient")
                        .and("score").as("score"),
                // 동일 재료명 중 가장 높은 score 선택
                Aggregation.group("ingredient")
                        .max("score").as("score"),
                Aggregation.project()
                        .andExpression("$_id").as("ingredient")
                        // score 기준 내림차순
                        .and("score").as("score"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "score")),
                Aggregation.limit(10)
        );

        AggregationResults<AutocompleteIngredientDto> results =
                mongoTemplate.aggregate(aggregation, collectionName, AutocompleteIngredientDto.class);

        return results.getMappedResults();
    }

    // 레시피명 기반 자동 완성
    @Override
    public List<AutocompleteRecipeNameDto> getResultAboutRecipeName(String term) {

        // highligth 설정
        Document recipeSearchStage = new Document("$search",
                new Document("index", "autocomplete_kr")
                        .append("autocomplete", new Document("query", term)
                                .append("path", "recipeName")
                                .append("tokenOrder", "any"))
                        .append("highlight", new Document("path", "recipeName")));
        // score json
        Document addFieldsStage = new Document("$addFields",
                new Document("score", new Document("$meta", "searchScore")));
        // highlight + score 만 projection
        Document highlightsProjectStage = new Document("$project",
                new Document("highlights", new Document("$meta", "searchHighlights"))
                        .append("score", 1));

        Document filterHits = new Document("$filter",
                new Document("input", "$$this.texts")
                        .append("cond", new Document("$eq", List.of("$$this.type", "hit"))));

        Document innerReduce = new Document("$reduce",
                new Document("input", "$highlights")
                        .append("initialValue", List.of())
                        .append("in", new Document("$concatArrays", List.of("$$value", filterHits))));

        Document matchedTextExpression = new Document("$reduce",
                new Document("input", innerReduce)
                        .append("initialValue", "")
                        .append("in", new Document("$concat", List.of("$$value", "$$this.value"))));

        Document matchedTextStage = new Document("$addFields",
                new Document("matchedText", matchedTextExpression));
        // prefix 보장
        Document matchedTextMatchStage = new Document("$match",
                new Document("matchedText",
                        new Document("$regex", "^%s".formatted(term))
                                .append("$options", "i")));

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.stage(recipeSearchStage),
                Aggregation.stage(addFieldsStage),
                Aggregation.stage(highlightsProjectStage),
                Aggregation.stage(matchedTextStage),
                Aggregation.stage(matchedTextMatchStage),
                Aggregation.project()
                        .andExpression("$matchedText").as("recipeName")
                        .and("score").as("score"),
                Aggregation.group("recipeName")
                        .max("score").as("score"),
                Aggregation.project()
                        .andExpression("$_id").as("recipeName")
                        .and("score").as("score"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "score")),
                Aggregation.limit(10)
        );

        AggregationResults<AutocompleteRecipeNameDto> results
                = mongoTemplate.aggregate(aggregation, collectionName, AutocompleteRecipeNameDto.class);

        return results.getMappedResults();
    }

    private String filterIngredientExpression(String term) {
        return """
                {
                    "$filter": {
                        "input": "$ingredientList",
                        "cond": {
                            "$regexMatch": {
                                "input": "$$this",
                                "regex": "^%s"
                            }
                        }
                    }
                }
                """.formatted(term);
    }
}
