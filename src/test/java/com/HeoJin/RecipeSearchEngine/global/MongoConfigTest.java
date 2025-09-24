package com.HeoJin.RecipeSearchEngine.global;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class MongoConfigTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${mongo.collectionName}")
    private String collectionName;

    @Test
    @DisplayName("mongoTemplate 관련 주입 테스트")
    void test1() {

        // given

        // when
        // then
        assertThat(mongoTemplate).isNotNull();
        assertThat(mongoTemplate).isInstanceOf(MongoTemplate.class);
        assertThat(mongoTemplate.getDb()).isNotNull();
    }

    @Test
    @DisplayName("application-test Value 값 테스트")
    void test2() {
        // given

        // when

        // then
        assertThat(collectionName).isNotNull();
        assertThat(collectionName).isEqualTo("testCollection");
    }
}
