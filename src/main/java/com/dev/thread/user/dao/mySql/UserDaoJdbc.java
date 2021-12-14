package com.dev.thread.user.dao.mySql;

import com.dev.thread.user.model.User;

public interface UserDaoJdbc {
    User create(User user);
}
