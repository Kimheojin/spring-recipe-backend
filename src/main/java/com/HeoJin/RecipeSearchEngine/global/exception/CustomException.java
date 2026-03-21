package com.HeoJin.RecipeSearchEngine.global.exception;


import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class CustomException extends RuntimeException {

    public final Map<String, String> validation = new HashMap<>();


    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, Throwable cause) {// cause -> 부모 exception
        super(message, cause);
    }
    // 오버라이드 대상
    public abstract int getStatusCode();

    public void addValidation(String fieldName, String message) {
        validation.put(fieldName, message);
    }
}
