package com.aswinayyappadas.exceptions;

public class ExceptionHandler extends Exception {
    public ExceptionHandler(String message) {
        super(message);
    }
    public ExceptionHandler(String message, Throwable cause) {
        super(message, cause);
    }
}
