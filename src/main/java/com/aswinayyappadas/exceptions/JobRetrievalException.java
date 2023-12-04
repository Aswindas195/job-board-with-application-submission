package com.aswinayyappadas.exceptions;

public class JobRetrievalException extends Exception {

    public JobRetrievalException(String message) {
        super(message);
    }

    public JobRetrievalException(String message, Throwable cause) {
        super(message, cause);
    }
}
