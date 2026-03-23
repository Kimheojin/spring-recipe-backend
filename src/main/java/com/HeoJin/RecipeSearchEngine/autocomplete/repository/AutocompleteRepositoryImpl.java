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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.regex.Pattern;

@Repository
@Primary
@RequiredArgsConstructor
public class AutocompleteRepositoryImpl implements AutocompleteRepository {

    private final MongoTemplate mongoTemplate;

    @Value("${mongo.collectionName}")
    private String collectionName;

    /**
     * 재료명 기반 자동 완성 (Prefix Match)
     */
    @Override
    public List<AutocompleteIngredientDto> getResultAboutIngredient(String term) {
        String escapedTerm = escapeRegex(term);

        Aggregation aggregation = Aggregation.newAggregation(
                // 1. Atlas Search: 후보군 추출
                Aggregation.stage(Document.parse("""
                        {
                          "$search": {
                            "index": "autocomplete_kr",
                            "autocomplete": {
                              "query": "%s",
                              "path": "ingredientList"
                            }
                          }
                        }
                        """.formatted(term))),

                // 2. 검색 점수 추가
                Aggregation.stage(new Document("$addFields", new Document("score", new Document("$meta", "searchScore")))),

                // 3. 배열 해제 및 접두어 필터링 (UX 핵심)
                Aggregation.unwind("ingredientList"),
                Aggregation.match(Criteria.where("ingredientList").regex("^" + escapedTerm, "i")),

                // 4. 중복 제거 및 점수 보존
                Aggregation.group("ingredientList").max("score").as("score"),

                // 5. 프로젝션 및 정렬
                Aggregation.project()
                        .andExpression("$_id").as("ingredient")
                        .and("score").as("score"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "score")),
                Aggregation.limit(10)
        );

        return mongoTemplate.aggregate(aggregation, collectionName, AutocompleteIngredientDto.class).getMappedResults();
    }

    /**
     * 레시피명 기반 자동 완성 (Prefix Match)
     */
    @Override
    public List<AutocompleteRecipeNameDto> getResultAboutRecipeName(String term) {
        String escapedTerm = escapeRegex(term);

        Aggregation aggregation = Aggregation.newAggregation(
                // 1. Atlas Search: 후보군 추출
                Aggregation.stage(Document.parse("""
                        {
                          "$search": {
                            "index": "autocomplete_kr",
                            "autocomplete": {
                              "query": "%s",
                              "path": "recipeName"
                            }
                          }
                        }
                        """.formatted(term))),

                // 2. 검색 점수 추가
                Aggregation.stage(new Document("$addFields", new Document("score", new Document("$meta", "searchScore")))),

                // 3. 접두어 필터링 (UX 핵심: 검색어로 시작하는 항목만 노출)
                Aggregation.match(Criteria.where("recipeName").regex("^" + escapedTerm, "i")),

                // 4. 중복 제거
                Aggregation.group("recipeName").max("score").as("score"),

                // 5. 프로젝션 및 정렬
                Aggregation.project()
                        .andExpression("$_id").as("recipeName")
                        .and("score").as("score"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "score")),
                Aggregation.limit(10)
        );

        return mongoTemplate.aggregate(aggregation, collectionName, AutocompleteRecipeNameDto.class).getMappedResults();
    }

    /**
     * 정규표현식 특수문자 이스케이프 처리
     */
    private String escapeRegex(String term) {
        return Pattern.quote(term);
    }
}