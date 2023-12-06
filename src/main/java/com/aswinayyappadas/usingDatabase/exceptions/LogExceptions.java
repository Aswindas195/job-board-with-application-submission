package com.aswinayyappadas.usingDatabase.exceptions;

import java.sql.SQLException;

public class LogExceptions {
    public void logSQLExceptionDetails(SQLException e) {
        System.err.println("SQL Exception Details:");
        System.err.println("SQL State: " + e.getSQLState());
        System.err.println("Error Code: " + e.getErrorCode());
        System.err.println("Message: " + e.getMessage());
    }
}
