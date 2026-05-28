package com.helene.backend.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {

    private boolean success;
    private int status;
    private String message;

    public static Response validationError(String message) {
        return new Response(false, 400, message);
    }

    public static Response generalError(int status, String message) {
        return new Response(false, status, message);
    }
}
