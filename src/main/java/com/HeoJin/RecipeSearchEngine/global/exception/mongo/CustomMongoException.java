package com.HeoJin.RecipeSearchEngine.global.exception.mongo;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


@Getter
public abstract class CustomMongoException extends RuntimeException{

    public final Map<String, String> validation = new HashMap<>();


    public CustomMongoException(String message){
        super(message);
    }

    public CustomMongoException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();

    public void addValidation(String fieldName, String message){
        validation.put(fieldName, message);
    }



}
