package com.HeoJin.RecipeSearchEngine.global.exception.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private final String message;
    private final int statusCode;

    @Builder.Default
    private final Map<String, String> validation = new HashMap<>();

    // 중복 코드 제거용, 별 의미 없
    public void addValidation(String field, String errorMessage) {
        this.validation.put(field, errorMessage);
    }
}
