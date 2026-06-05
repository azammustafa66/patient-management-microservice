package com.pm.patientservice.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pm.patientservice.config.AppEnvironment;
import lombok.Getter;

import java.io.PrintWriter;
import java.io.StringWriter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIError extends APIResponse<Void> {

    private final String stack;

    public APIError(int statusCode, String message) {
        this(statusCode, message, null);
    }

    public APIError(int statusCode, String message, Throwable cause) {
        super(statusCode, null, message);
        this.stack = (AppEnvironment.isDev() && cause != null) ? stackTraceOf(cause) : null;
    }

    private static String stackTraceOf(Throwable cause) {
        StringWriter sw = new StringWriter();
        cause.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
