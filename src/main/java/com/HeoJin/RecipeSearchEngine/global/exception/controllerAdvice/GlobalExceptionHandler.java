package com.HeoJin.RecipeSearchEngine.global.exception.controllerAdvice;


import com.HeoJin.RecipeSearchEngine.global.exception.CustomException;
import com.HeoJin.RecipeSearchEngine.global.exception.dto.ErrorResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e){
        int statusCode = e.getStatusCode();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .statusCode(statusCode)
                .build();
        // 존재하는 경우에만 목사
        if(!e.getValidation().isEmpty()) {
            e.getValidation().forEach(errorResponse::addValidation);
        }

        return ResponseEntity.status(statusCode)
                .body(errorResponse);
    }
}
