package com.dev.thread.user.worker;

import com.dev.thread.user.dao.mySql.UserDaoJdbcImpl;
import com.dev.thread.user.model.User;
import com.dev.thread.user.service.UserService;

import org.springframework.stereotype.Service;

@Service
public class MainWorker {
    private final UserDaoJdbcImpl userDaoJdbc;
    private final UserService userService;

    public MainWorker(UserDaoJdbcImpl userDaoJdbc,
                      UserService userService) {
        this.userDaoJdbc = userDaoJdbc;
        this.userService = userService;
    }

    public void testThread(String version) {
        addToMySql();
        addToMongo();
    }

    private void addToMySql() {
        User user1 = new User();
        user1.setName("Iryna");
        user1.setSum(50.0);

        User user2 = new User();
        user2.setName("Oleg");
        user2.setSum(100.0);

        User user3 = new User();
        user3.setName("Carla");
        user3.setSum(20.0);

        userDaoJdbc.create(user1);
        userDaoJdbc.create(user2);
        userDaoJdbc.create(user3);
    }

    private void addToMongo() {
        User user1 = new User();
        user1.setName("Iryna");
        user1.setSum(50.0);

        User user2 = new User();
        user2.setName("Oleg");
        user2.setSum(100.0);

        User user3 = new User();
        user3.setName("Carla");
        user3.setSum(20.0);

        userService.add(user1);
        userService.add(user2);
        userService.add(user3);
    }
}
