package com.HeoJin.RecipeSearchEngine.global.exception.mongo;

import org.springframework.http.HttpStatus;

public class CustomMongoNullException extends CustomMongoException{


    private final static String MESSAGE = "NULL 값 반환: ";

    public CustomMongoNullException(String message) {
        super(MESSAGE + message);
    }


    @Override
    public int getStatusCode() {
        return HttpStatus.NOT_FOUND.value();
    }
}
