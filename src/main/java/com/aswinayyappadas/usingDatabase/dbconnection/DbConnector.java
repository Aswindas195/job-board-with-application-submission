package com.aswinayyappadas.usingDatabase.dbconnection;

import com.aswinayyappadas.usingDatabase.exceptions.LogExceptions;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;



public class DbConnector {
    private static String JDBC_URL = String.format("jdbc:postgresql://%s:%s/%s", getHost(), getPort(), getDbname());
    private static final String USERNAME = getUsername();

    private static final String PASSWORD = getPassword();

    private static String getHost() {
        return System.getenv("HOST");
    }
    private static String getUsername() {
        return System.getenv("DBUSER");
    }
    private static String getPassword() {
        return System.getenv("PASSWORD");
    }
    private static String getPort() {
        return System.getenv("PORT");
    }
    private static String getDbname() {
        return System.getenv("DBNAME");
    }
    static {
        try {
            // Register the PostgreSQL driver
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  // Handle this exception properly in a real application
        }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public static void main(String[] args) {
        Connection connection = null;

        System.out.printf(" :::"+ JDBC_URL + "\n" + USERNAME + "\n" + PASSWORD);

        try {
            // Attempt to establish a connection
            connection = getConnection();

            // Check if the connection is successful
            if (connection != null) {
                System.out.println("Connected to the database!");

                // Get metadata about the database
                DatabaseMetaData metaData = connection.getMetaData();

                // Retrieve all table names
                ResultSet resultSet = metaData.getTables(null, null, "%", new String[]{"TABLE"});

                // Display the table names
                System.out.println("Tables in the database:");
                while (resultSet.next()) {
                    String tableName = resultSet.getString("TABLE_NAME");
                    System.out.println(tableName);
                }
            } else {
                System.out.println("Failed to connect to the database!");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately in a real application
        } finally {
            try {
                // Close the connection in a finally block to ensure it's always closed
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("Connection closed.");
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Handle exceptions appropriately in a real application
            }
        }
    }
}
