package com.example.taskManager.exception;

import com.example.taskManager.dto.responses.ErrorResponse;
import com.example.taskManager.exception.types.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 401 - UNAUTHORIZED
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            UnauthorizedException e,
            HttpServletRequest request
    ) {
        return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED, request);
    }

    // 403 - FORBIDDEN
    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ErrorResponse> handlePermissionDenied(
            PermissionDeniedException e,
            HttpServletRequest request
    ) {
        return buildErrorResponse(e.getMessage(), HttpStatus.FORBIDDEN, request);
    }

    // 404 - NOT FOUND
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            UserNotFoundException e,
            HttpServletRequest request
    ) {
        return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    // 400 - BAD REQUEST
    @ExceptionHandler({DuplicateResourceException.class, InvalidCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(
            RuntimeException e,
            HttpServletRequest request
    ) {
        return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    // 400 - Ошибки валидации
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        return buildErrorResponse(message, HttpStatus.BAD_REQUEST, request);
    }

    // 500 - INTERNAL SERVER ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception e,
            HttpServletRequest request
    ) {
        return buildErrorResponse(
                "Внутренняя ошибка сервера",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String message,
            HttpStatus status,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .status(status.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }
}