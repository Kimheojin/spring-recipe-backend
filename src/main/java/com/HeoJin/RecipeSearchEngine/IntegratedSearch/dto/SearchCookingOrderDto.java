package com.HeoJin.RecipeSearchEngine.IntegratedSearch.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class SearchCookingOrderDto {
    private int step;
    private String instruction;

}
