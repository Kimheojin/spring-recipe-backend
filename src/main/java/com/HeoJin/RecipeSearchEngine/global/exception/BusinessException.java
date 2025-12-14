package com.HeoJin.RecipeSearchEngine.global.exception;

import com.HeoJin.RecipeSearchEngine.global.exception.ErrorCode.EnumErrorCode;

public class BusinessException extends CustomException {

    private final EnumErrorCode errorCode;

    public BusinessException(EnumErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(EnumErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    @Override
    public int getStatusCode() {
        return errorCode.getStatus();
    }

    public String getErrorCode() {
        return errorCode.getCode();
    }
}