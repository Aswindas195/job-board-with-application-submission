package com.aswinayyappadas.exceptions;

public class JobUpdateException extends Throwable {
    public JobUpdateException(String message) {
        super(message);
    }
    public JobUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
