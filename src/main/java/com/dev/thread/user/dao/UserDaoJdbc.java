package com.dev.thread.user.dao;

import com.dev.thread.user.exception.DataProcessingException;
import com.dev.thread.user.model.User;
import com.dev.thread.user.util.ConnectionUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Repository
public class UserDaoJdbc {

    public void save(User user) {
        try (Connection connection = ConnectionUtil.getConnectionSql()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO users (name, sum) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getName());
            statement.setDouble(2, user.getSum());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Adding user with id "
                    + user.getId() + " was failed. ", e);
        }
    }

    public void saveAll(List<User> users) {
        try (Connection connection = ConnectionUtil.getConnectionSql()) {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO users (user_name, sum) VALUES (?, ?)");
            int i = 0;
            for (User user : users) {
                ps.setString(1, user.getName());
                ps.setDouble(2, user.getSum());
                ps.addBatch();
                i++;
                if (i == users.size()) {
                    ps.executeBatch();
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Adding users was failed. ", e);
        }
    }

    public void clearTable() {
        try (Connection connection = ConnectionUtil.getConnectionSql()) {
            PreparedStatement statement = connection.prepareStatement(
                    "TRUNCATE TABLE  users;",
                    Statement.RETURN_GENERATED_KEYS);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Clear table was failed. ", e);
        }
    }
}
