package com.HeoJin.RecipeSearchEngine.integratedSearch.controller;


import com.HeoJin.RecipeSearchEngine.Init.TestService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class IntegratedSearchControllerTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestService testService;

    @Value("${mongo.collectionName}")
    private String collectionName;

    @BeforeEach
    void init() {
        // 데이터 없는 경우에만 삽입
        // 어처피 select 로직 밖에 없을듯
        if (mongoTemplate.count(new Query(), collectionName) == 0) {
            testService.insertInitData();
        }
    }


}
