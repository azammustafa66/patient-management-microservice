package com.pm.patientservice.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T> {
    private final int statusCode;
    private final T data;
    private final boolean success;
    private final String message;

    public APIResponse(int statusCode, T data) {
        this(statusCode, data, defaultMessage(statusCode));
    }

    protected APIResponse(int statusCode, T data, String message) {
        this.statusCode = statusCode;
        this.data = data;
        this.success = statusCode < 400;
        this.message = message;
    }

    private static String defaultMessage(int statusCode) {
        try {
            return HttpStatus.valueOf(statusCode).getReasonPhrase();
        } catch (IllegalArgumentException ex) {
            return "Unknown Status";
        }
    }
}
