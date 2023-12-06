package com.aswinayyappadas.usingDatabase.services;

import com.aswinayyappadas.usingDatabase.dbconnection.DbConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KeyServices {
    public void storeSecretKeyByEmail(String email, String secretKey) throws SQLException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "UPDATE users SET jwt_secret_key = ? WHERE email = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, secretKey);
                preparedStatement.setString(2, email);

                preparedStatement.executeUpdate();
            }
        }
    }
    public String getJwtSecretKeyByEmail(String email) throws SQLException {
        try (Connection connection = DbConnector.getConnection()) {
            String sql = "SELECT jwt_secret_key FROM users WHERE email = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, email);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("jwt_secret_key");
                    }
                }
            }
        }

        // Return null or handle accordingly based on your requirements
        return null;
    }

}
