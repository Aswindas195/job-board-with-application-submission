package com.aswinayyappadas.exceptions;

public class UserRetrievalException extends Exception {
    public UserRetrievalException(String message) {
        super(message);
    }

    public UserRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
