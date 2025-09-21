package com.HeoJin.RecipeSearchEngine.IntegratedSearch.service;


import com.HeoJin.RecipeSearchEngine.IntegratedSearch.repository.IntegrationSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IntegratedSearchService {

    private final IntegrationSearchRepository integrationSearchRepository;
}
