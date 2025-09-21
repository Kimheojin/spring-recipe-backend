package com.HeoJin.RecipeSearchEngine.IntegratedSearch.repository;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!test")
public class IntegrationSearchRepositoryImpl implements IntegrationSearchRepository {
        // count 값도 같이 반환하기
        //

}
