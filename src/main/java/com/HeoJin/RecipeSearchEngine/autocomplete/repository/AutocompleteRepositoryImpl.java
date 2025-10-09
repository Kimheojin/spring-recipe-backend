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
    // 이거 빈도수도 한번 생각해 보는 것도 좋을듯

    private final MongoTemplate mongoTemplate;

    @Value("${mongo.collectionName}")
    private String collectionName;

    // 재료기반 -> 단어 매핑을 목적으로 생각하자
    // count 생각 안해도 될듯
    @Override
    public List<AutocompleteIngredientDto> getResultAboutIngredient(String term) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.stage(Document.parse("""
                    {
                        "$search": {
                            "index": "ingredient_autocomplete_kr",
                             "autocomplete": {
                                "query": "%s",
                                "path": "ingredientList",
                                "tokenOrder": "any"
                                }
                            }
                    }
                    """.formatted(term))),
                Aggregation.stage(Document.parse("""
                    {
                        "$addFields": {
                            "score": { "$meta": "searchScore" }
                        }
                    }
                    """)),
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
                    """.formatted(term)).as("matchingIngredients")
                        .and("score").as("score"),
                Aggregation.unwind("matchingIngredients"),
                Aggregation.project()
                        .andExpression("$matchingIngredients").as("ingredient")
                        .and("score").as("score"),
                Aggregation.group("ingredient")
                        .max("score").as("score"),
                Aggregation.project()
                        .andExpression("$_id").as("ingredient")
                        .and("score").as("score"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "score")),
                Aggregation.limit(10)
        );

        AggregationResults<AutocompleteIngredientDto> results =
                mongoTemplate.aggregate(aggregation, collectionName, AutocompleteIngredientDto.class);

        return results.getMappedResults();
    }

    // recipe name 데이터가 더러워서 이렇게 하는 게 맞을듯
    @Override
    public List<AutocompleteRecipeNameDto> getResultAboutRecipeName(String term) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.stage(Document.parse("""
            {
                "$search": {
                    "index": "recipeName_autocomplete_kr",
                    "autocomplete": {
                        "query": "%s",
                        "path": "recipeName",
                        "tokenOrder": "any"
                    },
                    "highlight": {
                        "path": "recipeName"
                    }
                }
            }
            """.formatted(term))),
                Aggregation.stage(Document.parse("""
            {
                "$addFields": {
                    "score": { "$meta": "searchScore" }
                }
            }
            """)),
                Aggregation.stage(Document.parse("""
            {
                "$project": {
                    "highlights": { "$meta": "searchHighlights" },
                    "score": 1
                }
            }
            """)),
                // 기존 matchedText 추출 로직...
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

        AggregationResults<AutocompleteRecipeNameDto> results
                = mongoTemplate.aggregate(aggregation, collectionName, AutocompleteRecipeNameDto.class);

        return results.getMappedResults();
    }
}
