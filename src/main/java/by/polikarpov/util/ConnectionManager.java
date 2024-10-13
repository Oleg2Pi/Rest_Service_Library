package by.polikarpov.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {
    public static Connection getConnection() throws SQLException {
        Properties properties = new Properties();
        try {
            properties.load(ConnectionManager.class.getClassLoader()
                    .getResourceAsStream("application.properties"));
            return DriverManager.getConnection(
                    properties.getProperty("url"),
                    properties.getProperty("user"),
                    properties.getProperty("password")
            );
        } catch (IOException e) {
            throw new SQLException("Не загрузились данные БД", e);
        }
    }
}
