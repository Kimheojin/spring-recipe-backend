package com.HeoJin.RecipeSearchEngine.basicSearch.repository;


import com.HeoJin.RecipeSearchEngine.basicSearch.dto.RecipeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
public class RecipeBasicRepositoryImpl implements RecipeBasicRepository {
    private final MongoTemplate mongoTemplate;

    @Value("${mongo.collectionName}")
    private String collectionName;


    @Override
    public RecipeResponseDto getRecipe(String objectId) {

        return mongoTemplate.findById(objectId, RecipeResponseDto.class, collectionName);
    }

    // 첫 페이지 반환
    @Override
    public List<RecipeResponseDto> getFirstPageRecipe(int pageSize) {
        return List.of();
    }

    @Override
    public List<RecipeResponseDto> getPageRecipe(String objectId, int page, int pageSize) {
        return List.of();
    }


}
