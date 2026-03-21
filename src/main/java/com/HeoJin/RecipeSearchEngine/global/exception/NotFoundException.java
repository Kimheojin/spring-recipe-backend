package com.HeoJin.RecipeSearchEngine.global.exception;


public class NotFoundException extends CustomException {

    private static final int STATUS_CODE = 404;

    public NotFoundException(String message){
        super(message);
    }

    public NotFoundException(String message, Throwable cause){
        super(message, cause);
    }

    @Override
    public int getStatusCode(){
        return STATUS_CODE;
    }
}
