package com.HeoJin.RecipeSearchEngine.basicSearch.repository;


import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeResponseDto;
import com.HeoJin.RecipeSearchEngine.global.entity.Recipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Profile("!test")
@RequiredArgsConstructor
public class RecipeBasicRepositoryImpl implements RecipeBasicRepository {
    private final MongoTemplate mongoTemplate;

    @Value("${mongo.collectionName}")
    private String collectionName;


    @Override
    public RecipeResponseDto getRecipe(String objectId) {
        ObjectId id = new ObjectId(objectId);

        Recipe singleRecipe = mongoTemplate.findById(id, Recipe.class, collectionName);

        return RecipeResponseDto.from(singleRecipe);
    }

    // 첫 페이지 반환
    @Override
    public List<RecipeResponseDto> getFirstPageRecipe(int pageSize) {
        Query query = new Query();
        query.with(Sort.by(Sort.Direction.ASC, "_id"));
        query.limit(pageSize);
        query.withHint("_id_");

        List<Recipe> recipes = mongoTemplate.find(query, Recipe.class, collectionName);

        return recipes.stream()
                .map(RecipeResponseDto::from)
                .collect(Collectors.toList());

    }
    // 음수 페이지 증가하는 경우 (이전 페이지로)
    @Override
    public List<RecipeResponseDto> getLtPageRecipe(int page, int pageSize, String objectId) {
        Query query = new Query();
        int absPage = Math.abs(page);

        query.addCriteria(Criteria.where("_id").lte(new ObjectId(objectId)));
        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        query.limit(pageSize);
        query.skip(absPage * pageSize);
        query.withHint("_id_");

        List<Recipe> recipes = mongoTemplate.find(query, Recipe.class, collectionName);



        List<RecipeResponseDto> result = recipes.stream()
                .map(RecipeResponseDto::from)
                .collect(Collectors.toList());
        Collections.reverse(result);
        return result;
        }


    @Override
    public List<RecipeResponseDto> getGtPageRecipe(int page, int pageSize, String objectId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").gt(new ObjectId(objectId)));
        query.with(Sort.by(Sort.Direction.ASC, "_id"));
        query.skip((page -1)* pageSize);
        query.limit(pageSize);
        query.withHint("_id_");
        List<Recipe> recipes = mongoTemplate.find(query, Recipe.class, collectionName);

        return recipes.stream()
                .map(RecipeResponseDto::from)
                .collect(Collectors.toList());
    }


}