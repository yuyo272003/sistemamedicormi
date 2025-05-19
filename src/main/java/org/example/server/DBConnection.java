package org.example.server;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    // String JDBC con SSL obligatorio para Railway
    private static final String URL =
            "jdbc:mysql://root:UDCWbXjREWDzHFhBwHCMcEJUhYgHVwoY@crossover.proxy.rlwy.net:58173/railway";
    private static final String USER = "root";
    private static final String PASS = "UDCWbXjREWDzHFhBwHCMcEJUhYgHVwoY";

    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
