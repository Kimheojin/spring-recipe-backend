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
        // 검색어 내 특수문자 있으면 오류 뜨 듯
        String escapedTerm = term.replaceAll("([\\\\\\\\\\\\\\\\.*+?^${}()|\\\\[\\\\]])", "\\\\\\\\$1");

        Aggregation aggregation = Aggregation.newAggregation(
                // 검색 (Atlas Search)
                Aggregation.stage(Document.parse("""
                        {
                          "$search": {
                            "index": "autocomplete_kr",
                            "autocomplete": {
                              "query": "%s",
                              "path": "ingredientList",
                              "tokenOrder": "any"
                            }
                          }
                        }
                        """.formatted(term))),

                // 검색 점수 추가
                Aggregation.stage(Document.parse("{ '$addFields': { 'score': { '$meta': 'searchScore' } } }")),

                // 배열 풀기 및 매칭되는 재료만 필터링
                Aggregation.unwind("ingredientList"),
                Aggregation.stage(Document.parse("""
                        {
                          "$match": {
                            "ingredientList": { "$regex": "^%s", "$options": "i" }
                          }
                        }
                        """.formatted(escapedTerm))),

                // 중복 제거 (여러 레시피에 같은 재료가 있을 경우 최고 점수 유지)
                Aggregation.group("ingredientList")
                        .max("score").as("score"),

                // 결과 필드 매핑 및 정렬
                Aggregation.project()
                        .andExpression("$_id").as("ingredient")
                        .and("score").as("score"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "score")),
                Aggregation.limit(10)
        );

        return mongoTemplate.aggregate(aggregation, collectionName, AutocompleteIngredientDto.class).getMappedResults();
    }

    // 레시피명 기반 자동 완성
    @Override
    public List<AutocompleteRecipeNameDto> getResultAboutRecipeName(String term) {
        Aggregation aggregation = Aggregation.newAggregation(
                // 검색 (Atlas Search)
                Aggregation.stage(Document.parse("""
                        {
                          "$search": {
                            "index": "autocomplete_kr",
                            "autocomplete": {
                              "query": "%s",
                              "path": "recipeName",
                              "tokenOrder": "any"
                            }
                          }
                        }
                        """.formatted(term))),

                // 검색 점수 추가
                Aggregation.stage(Document.parse("{ '$addFields': { 'score': { '$meta': 'searchScore' } } }")),

                // 중복 제거 (동일한 레시피명이 있을 경우 대비)
                Aggregation.group("recipeName")
                        .max("score").as("score"),

                // 결과 필드 매핑 및 정렬
                Aggregation.project()
                        .andExpression("$_id").as("recipeName")
                        .and("score").as("score"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "score")),
                Aggregation.limit(10)
        );

        return mongoTemplate.aggregate(aggregation, collectionName, AutocompleteRecipeNameDto.class).getMappedResults();
    }
}

