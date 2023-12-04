package com.aswinayyappadas.exceptions;

public class JobApplicationException extends Exception{
    public JobApplicationException(String message) {
        super(message);
    }
    public JobApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
