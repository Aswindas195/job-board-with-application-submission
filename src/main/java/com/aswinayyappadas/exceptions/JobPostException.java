package com.aswinayyappadas.exceptions;

public class JobPostException extends Exception {

    public JobPostException(String message) {
        super(message);
    }

    public JobPostException(String message, Throwable cause) {
        super(message, cause);
    }
}
