package com.HeoJin.RecipeSearchEngine.basicSearch.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CookingOrderDto {
    private int step;
    private String instruction;

}
