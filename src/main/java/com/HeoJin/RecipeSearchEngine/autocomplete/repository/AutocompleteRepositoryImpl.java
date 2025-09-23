package com.HeoJin.RecipeSearchEngine.autocomplete.repository;


import com.HeoJin.RecipeSearchEngine.autocomplete.dto.AutocompleteIngredientDto;
import com.HeoJin.RecipeSearchEngine.autocomplete.dto.AutocompleteRecipeNameDto;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
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


    // 재료 기반 개느림
    // ingredientList 따로 전처리 해서 하는 식으로 리팩토링 해야할듯
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
                                    "tokenOrder": "sequential"
                                    }
                                }
                        }
                        """.formatted(term))) ,
                Aggregation.project()
                        .andExpression("""
                        {
                            "$filter": {
                                "input": "$ingredientList",
                                "cond": {
                                    "$regexMatch": {
                                        "input": "$$this",
                                        "regex": "%s"
                                    }
                                }
                            }df
                        }
                        """.formatted(term)).as("matchingIngredients"),
                Aggregation.unwind("matchingIngredients"),
                Aggregation.project().andExpression("$matchingIngredients").as("ingredient"),
                Aggregation.group("ingredient"),
                Aggregation.project().andExpression("$_id").as("ingredient"),// 중복 제거
                Aggregation.limit(10)
        );
        AggregationResults<AutocompleteIngredientDto> results = mongoTemplate.aggregate(aggregation, collectionName, AutocompleteIngredientDto.class);


        return results.getMappedResults(); // list 형태로 반환

    }

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
                                    "tokenOrder": "sequential"
                                    }
                                }
                        }
                        """.formatted(term))),
                Aggregation.project()
                                .andExpression("$recipeName").as("recipeName"),
                Aggregation.group("recipeName"), // 중복 제거
                Aggregation.project().andExpression("$_id").as("recipeName"),
                Aggregation.limit(10)
        );

        AggregationResults<AutocompleteRecipeNameDto> results = mongoTemplate.aggregate(aggregation, collectionName, AutocompleteRecipeNameDto.class);


        return results.getMappedResults();



    }
}
