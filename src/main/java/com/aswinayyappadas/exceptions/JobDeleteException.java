package com.aswinayyappadas.exceptions;

public class JobDeleteException extends Exception {

    public JobDeleteException(String message) {
        super(message);
    }

    public JobDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
