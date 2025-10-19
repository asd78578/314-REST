package ru.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.demo.exception.UserUsernameExistException;
import ru.demo.util.ApiResponse;

import javax.persistence.EntityNotFoundException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Ошибка: пользователь с таким именем уже существует
    @ExceptionHandler(UserUsernameExistException.class)
    public ResponseEntity<ApiResponse> handleUserExists(UserUsernameExistException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse(ex.getMessage()));
    }

    // Ошибка валидации (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(errorMessages));
    }

    // Ошибка: неверный формат параметра (например, id не число)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse("Invalid parameter: " + ex.getName()));
    }

    // Общий обработчик всех непредусмотренных ошибок
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        ex.printStackTrace(); // можно убрать в продакшене
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("Internal server error: " + ex.getMessage()));
    }

    // Возвращает ошибку 404, если пользователь не найден
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(ex.getMessage()));
    }

}
