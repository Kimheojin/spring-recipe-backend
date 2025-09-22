package com.HeoJin.RecipeSearchEngine.global.exception.controllerAdvice;


import com.HeoJin.RecipeSearchEngine.global.exception.mongo.CustomMongoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.HeoJin.RecipeSearchEngine.global.exception.dto.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomMongoException.class)
    public ResponseEntity<ErrorResponse> handleCustomMongoException(CustomMongoException e){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .statusCode(e.getStatusCode())
                .build();

        if(!e.getValidation().isEmpty()) {
            e.getValidation().forEach(errorResponse::addValidation);
        }

        return ResponseEntity.status(e.getStatusCode())
                .body(errorResponse);
    }
}
