package com.dev.thread.user.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtil {
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver is not installed", e);
        }
    }

    public static Connection getConnectionSql() {
        Connection conn;
        Properties connectionProps = new Properties();
        connectionProps.put("user","root");
        connectionProps.put("password", "1234");
        String url = "jdbc:mysql://localhost:3306/thread_user?serverTimezone=UTC";
        try {
            conn = DriverManager.getConnection(url, connectionProps);
        } catch (SQLException e) {
            throw new RuntimeException("Can not connect to the data base. ", e);
        }
        return conn;
    }
}
