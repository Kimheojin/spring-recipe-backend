package com.HeoJin.RecipeSearchEngine.global.exception;

public class ExternalServiceException extends CustomException {

    private final int statusCode;

    public ExternalServiceException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }


    public ExternalServiceException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }
}
