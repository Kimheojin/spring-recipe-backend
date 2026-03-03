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
        Aggregation aggregation = Aggregation.newAggregation(
                // search 연산자
                // 문서 단위 검색
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

                // Score 필드 추가 파이프
                // Document 에 붙음
                Aggregation.stage(Document.parse("""
                        {
                          "$addFields": { "score": { "$meta": "searchScore" } }
                        }
                        """)),

                // 요소 단위 추출
                // matchingIngredients 배열 + score 필드 (아직 배열 형태)
                Aggregation.project()
                        .andExpression("""
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
                                """.formatted(term))
                        .as("matchingIngredients")
                        .and("score").as("score"),

                // 배열 품
                Aggregation.unwind("matchingIngredients"),
                // score 는 필드 별로
                Aggregation.project()
                        .andExpression("$matchingIngredients").as("ingredient")
                        .and("score").as("score"),
                // 중복 데이터 묶고, score max 값 지정
                Aggregation.group("ingredient")
                        .max("score").as("score"),

                // 필드 관련 연산자
                Aggregation.project()
                        // 재료명에 담긴 _id 필드를 가져와 ingredient 필드 이름 할당
                        // { "_id" : "사과" } -> { "ingredient" : "사과" }
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
                // search 연산자 + 하이라이트 옵션 활성화
                // 이건 전용 메서드가 아직 없어서 이렇게 해야됨
                Aggregation.stage(Document.parse("""
                        {
                          "$search": {
                            "index": "autocomplete_kr",
                            "autocomplete": {
                              "query": "%s",
                              "path": "recipeName",
                              "tokenOrder": "any"
                            },
                            "highlight": { "path": "recipeName" }
                          }
                        }
                        """.formatted(term))),

                // Document 에 메타데이터를 붙임
                Aggregation.stage(Document.parse("{ '$addFields': { 'score': { '$meta': 'searchScore' } } }")),
                // searchHighlights -> 이중 배열
                Aggregation.stage(Document.parse("{ '$project': { 'highlights': { '$meta': 'searchHighlights' }, 'score': 1 } }")),

                // reduce 연산자 -> 거의 함수
                // input -> 원본 파라미터
                // initValue -> 초깃값
                // in -> 함수 본문
                //$$this -> 현재 요소
                // $$ value -> 누적 변수 (그냥 지역변수 ㅡㄴ낌)
                Aggregation.stage(Document.parse("""
                        {
                          "$addFields": {
                            "matchedText": {
                              "$reduce": {
                                "input": {
                                  "$reduce": {
                                    "input": "$highlights",
                                    "initialValue": [],
                                    "in": {
                                      "$concatArrays": [
                                        "$$value",
                                        {
                                          "$filter": {
                                            "input": "$$this.texts",
                                            "cond": { "$eq": ["$$this.type", "hit"] }
                                          }
                                        }
                                      ]
                                    }
                                  }
                                },
                                "initialValue": "",
                                "in": { "$concat": ["$$value", "$$this.value"] }
                              }
                            }
                          }
                        }
                        """)),

                // regex
                Aggregation.stage(Document.parse("""
                        {
                          "$match": {
                            "matchedText": { "$regex": "^%s", "$options": "i" }
                          }
                        }
                        """.formatted(term))),


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

        return mongoTemplate.aggregate(aggregation, collectionName, AutocompleteRecipeNameDto.class).getMappedResults();
    }
}

