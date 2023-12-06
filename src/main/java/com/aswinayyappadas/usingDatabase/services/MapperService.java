package com.aswinayyappadas.usingDatabase.services;

import com.aswinayyappadas.usingDatabase.dbconnection.DbConnector;
import com.aswinayyappadas.usingDatabase.exceptions.LogExceptions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MapperService {
    private final LogExceptions logExceptions;

    public MapperService() {
        this.logExceptions = new LogExceptions();
    }

    public boolean isJobMappedToEmployer(int jobId, int employerId) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT COUNT(*) FROM joblistings WHERE jobid = ? AND employerid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, jobId);
                preparedStatement.setInt(2, employerId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0; // If count > 0, the job is mapped to the employer
                    }
                }
            }
        } catch (SQLException e) {
           logExceptions.logSQLExceptionDetails(e);
            // Handle the exception appropriately, e.g., log it or throw a custom exception
        }
        return false; // Default to false in case of an exception
    }
    public boolean isApplicationMappedToJobSeeker(int jobSeekerId, int jobId) {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT COUNT(*) FROM applications WHERE jobseekerid = ? AND jobid = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, jobSeekerId);
                preparedStatement.setInt(2, jobId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int count = resultSet.getInt(1);
                        return count > 0; // If count > 0, the application is mapped to the job seeker
                    }
                }
            }
        } catch (SQLException e) {
            logExceptions.logSQLExceptionDetails(e);
            // Handle the exception appropriately, e.g., log it or throw a custom exception
        }
        return false; // Default to false in case of an exception
    }

}
