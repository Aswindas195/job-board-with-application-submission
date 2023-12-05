package com.aswinayyappadas.exceptions;

public class ApplicationUpdateException extends Exception {
    public ApplicationUpdateException(String message) {
        super(message);
    }
    public ApplicationUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
