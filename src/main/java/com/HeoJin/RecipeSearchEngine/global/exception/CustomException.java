package com.HeoJin.RecipeSearchEngine.global.exception;


import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class CustomException extends RuntimeException {

    public final Map<String, String> validation = new HashMap<>();

    // 생성자
    public CustomException(String message){
        super(message);
    }
    // ovride 대상
    public abstract int getStatusCode();

    public void addValidation(String fieldName, String message){
        validation.put(fieldName, message);
    }
}
