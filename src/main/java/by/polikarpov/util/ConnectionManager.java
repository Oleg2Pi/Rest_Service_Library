package by.polikarpov.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing database connections.
 * This class loads the PostgreSQL driver and provides a method to retrieve
 * database connections using settings defined in a properties file.
 */
public class ConnectionManager {

    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";

    // Static block to load the JDBC driver when this class is loaded
    static {
        loadDriver();
    }

    /**
     * Loads the PostgreSQL JDBC driver.
     * This method is called during the static initialization of the class.
     *
     * @throws RuntimeException if the driver class cannot be found
     */
    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a connection to the database using the specified properties.
     *
     * @return a Connection object to the database
     * @throws SQLException if a database access error occurs, or the URL is null
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USERNAME_KEY),
                    PropertiesUtil.get(PASSWORD_KEY)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ConnectionManager() {
    }
}
