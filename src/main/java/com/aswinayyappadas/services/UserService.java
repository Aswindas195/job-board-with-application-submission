package com.aswinayyappadas.services;

import com.aswinayyappadas.dbconnection.DbConnector;
import com.aswinayyappadas.exceptions.RegistrationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserService {
    public int registerUser(String name, String email, String password, String role) throws RegistrationException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "INSERT INTO users (username, email, passwordhash, usertype) VALUES (?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, password);
                preparedStatement.setString(4, role);

                return preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RegistrationException("Error registering user.", e);
        }
    }
}
