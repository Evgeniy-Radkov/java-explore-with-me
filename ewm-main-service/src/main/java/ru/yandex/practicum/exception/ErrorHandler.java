package ru.yandex.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(final NotFoundException e) {
        log.warn("404 NotFound: {}", e.getMessage());
        ApiError body = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("The required object was not found.")
                .message(e.getMessage())
                .errors(List.of())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(final ConflictException e) {
        log.warn("409 Conflict: {}", e.getMessage());
        ApiError body = ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Integrity constraint has been violated.")
                .message(e.getMessage())
                .errors(List.of())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            BadRequestException.class,
            ValidationException.class,
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(final Exception e) {
        log.warn("400 BadRequest: {}", e.getMessage());
        ApiError body = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .errors(List.of())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleThrowable(final Throwable e) {
        log.error("500 Internal error", e);
        ApiError body = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .reason("Unexpected error has occurred.")
                .message(e.getMessage())
                .errors(List.of())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
