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

    public static Connection getConnectionSql() throws SQLException {
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

//    public static MongoCollection<Document> getConnectionMongo() {
//        String url = "mongodb+srv://root:1234@cluster0.pjzkv.mongodb.net/test";
//        MongoClient mongoClient = new MongoClient(new MongoClientURI(url));
//        MongoDatabase database = mongoClient.getDatabase("thread");
//        MongoCollection<Document> collection = database.getCollection("test");
//
//        System.out.println("Подключение успешное");
//
//        return collection;
//    }
}
