package com.aswinayyappadas.usingDatabase.dbconnection;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;



public class DbConnector {

//    private static final Dotenv dotenv = Dotenv.load();
//private static final String JDBC_URL = "jdbc:postgresql://"+  dotenv.get("HOST") + ":" + dotenv.get("PORT")+ "/" +dotenv.get("DBNAME");
//private static final String JDBC_URL = "jdbc:postgresql://0.0.0.0:5432/job_board_db" ;
    private static final String JDBC_URL = "jdbc:postgresql://db:5432/job_board_db" ;
    private static final String USERNAME = "postgres";
//    private static final String USERNAME = dotenv.get("DBUSER");
private static final String PASSWORD = "0091";
//    private static final String PASSWORD = dotenv.get("PASSWORD");


//    private static final String JDBC_URL = System.getenv("JDBC_URL");
//    private static final String USERNAME = System.getenv("DBUSER");
//    private static final String PASSWORD = System.getenv("PASSWORD");

    static {
        try {
            // Register the PostgreSQL driver
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  // Handle this exception properly in a real application
        }
    }
    public static Connection getConnection() throws SQLException {
        System.out.printf(" :::"+ JDBC_URL + "\n" + USERNAME + "\n" + PASSWORD);
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
