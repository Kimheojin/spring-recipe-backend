package com.HeoJin.RecipeSearchEngine.global.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Getter
public class Recipe {

    @Id
    private String id;

    private String recipeName;
    private String sourceUrl;
    private String siteIndex;
    @Builder.Default
    private List<String> ingredients = new ArrayList<>();
    @Builder.Default
    private List<cookingOrder> cookingOrderList = new ArrayList<>();
}
