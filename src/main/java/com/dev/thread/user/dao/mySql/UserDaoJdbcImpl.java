package com.dev.thread.user.dao.mySql;

import com.dev.thread.user.model.User;
import com.dev.thread.user.util.ConnectionUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class UserDaoJdbcImpl implements UserDaoJdbc {
    @Override
    public User create(User user) {
        try (Connection connection = ConnectionUtil.getConnectionSql()) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO users (user_name, sum) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getName());
            statement.setDouble(2, user.getSum());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getLong(1));
            }
        } catch (SQLException ignored) {

        }
        return user;
    }
}
//            throw new DataProcessingException("Adding user with id "
//                    + user.getId() + " was failed. ", e);