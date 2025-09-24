package com.HeoJin.RecipeSearchEngine.Init;


import com.HeoJin.RecipeSearchEngine.global.entity.Recipe;
import com.HeoJin.RecipeSearchEngine.global.entity.cookingOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("test")
public class TestService {

    private final MongoTemplate mongoTemplate;
    @Value("${mongo.collectionName}")
    private String collectionName;
    private static final String[] RECIPE_NAMES = {
        "김치찌개", "된장찌개", "불고기", "갈비찜", "삼겹살구이",
        "파스타", "피자", "햄버거", "치킨", "스테이크",
        "라면", "볶음밥", "비빔밥", "냉면", "짜장면"
    };

    private static final String[][] INGREDIENTS = {
        {"김치", "돼지고기", "두부", "양파"},
        {"된장", "두부", "호박", "감자"},
        {"소고기", "양파", "당근", "간장"},
        {"갈비", "무", "대파", "마늘"},
        {"삼겹살", "상추", "마늘", "쌈장"},
        {"파스타면", "토마토소스", "치즈", "바질"},
        {"피자도우", "토마토소스", "치즈", "페퍼로니"},
        {"햄버거빵", "패티", "양상추", "토마토"},
        {"닭고기", "튀김가루", "기름", "양념"},
        {"스테이크", "소금", "후추", "버터"},
        {"라면", "계란", "파", "김치"},
        {"밥", "계란", "파", "간장"},
        {"밥", "나물", "고추장", "참기름"},
        {"냉면", "육수", "오이", "배"},
        {"짜장면", "춘장", "양파", "돼지고기"}
    };

    public void insertInitData(){

        for (int i = 0; i < 15; i++) {
            List<cookingOrder> cookingSteps = Arrays.asList(
                cookingOrder.builder().step(1).instruction("재료를 준비합니다").build(),
                cookingOrder.builder().step(2).instruction("재료를 손질합니다").build(),
                cookingOrder.builder().step(3).instruction("조리를 시작합니다").build(),
                cookingOrder.builder().step(4).instruction("완성하여 그릇에 담습니다").build()
            );

            Recipe recipe = Recipe.builder()
                .recipeName(RECIPE_NAMES[i])
                .sourceUrl("https://example" + (i + 1) + ".com")
                .siteIndex("site" + (i + 1))
                .ingredientList(Arrays.asList(INGREDIENTS[i]))
                .cookingOrderList(cookingSteps)
                .build();

            mongoTemplate.save(recipe, collectionName);
        }
    }
}
