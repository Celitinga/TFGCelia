package com.helene.backend.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleValidationArgumentsErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String mapAsString = errors.keySet().stream()
                .map(key -> key + " = " + errors.get(key))
                .collect(Collectors.joining(", ", "{ ", " }"));

        return new ResponseEntity<>(
                Response.validationError(mapAsString),
                HttpStatus.BAD_REQUEST
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CreateEntityException.class)
    public ResponseEntity<Response> handleCreateEntityException(CreateEntityException ex) {

        logger.error(ex.getMessage(), ex);

        return new ResponseEntity<>(
                Response.generalError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundEntityException.class)
    public ResponseEntity<Response> handleNotFoundEntityException(NotFoundEntityException ex) {

        return new ResponseEntity<>(
                Response.generalError(HttpStatus.NOT_FOUND.value(), ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ErrorGenericoException.class)
    public ResponseEntity<Response> handleErrorGenericoException(ErrorGenericoException ex) {

        logger.error(ex.getMessage(), ex);

        return new ResponseEntity<>(
                Response.generalError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response> handleIllegalArgumentException(IllegalArgumentException ex) {

        logger.error(ex.getMessage(), ex);

        return new ResponseEntity<>(
                Response.generalError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response> handleAccessDeniedException(AccessDeniedException ex) {

        logger.error(ex.getMessage(), ex);

        return new ResponseEntity<>(
                Response.generalError(HttpStatus.FORBIDDEN.value(), "Acceso denegado"),
                HttpStatus.FORBIDDEN
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception ex) {

        logger.error(ex.getMessage(), ex);

        return new ResponseEntity<>(
                Response.generalError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno del servidor"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
