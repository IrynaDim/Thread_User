package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;

public class AbstractThread {
    private final Queue<String> dataFromFile;
    private final Map<String, User> map;
    private final UserDaoJdbc userDaoJdbc;
    private final UserDaoMongo userDaoMongo;

    public AbstractThread(Queue<String> dataFromFile, Map<String, User> map, UserDaoJdbc userDaoJdbc, UserDaoMongo userDaoMongo) {
        this.dataFromFile = dataFromFile;
        this.map = map;
        this.userDaoJdbc = userDaoJdbc;
        this.userDaoMongo = userDaoMongo;
    }

    public void addToMap() {
        while (!dataFromFile.isEmpty()) {
            String string;
            synchronized (dataFromFile) {
                string = dataFromFile.poll();
            }
            if (string != null) {
                String[] strings = string.split(",");
                String name = strings[1];
                Double sum = Double.valueOf(strings[2]);
                User user = new User();
                user.setName(name);
                user.setSum(sum);

                synchronized (map) {
                    map.put(name, map.containsKey(name)
                            ? new User(null, name, map.get(name).getSum() + sum) : user);
                }
            }
        }
    }

    public void addToDb() {
        userDaoJdbc.saveAll(new ArrayList<>(map.values()));
        userDaoMongo.saveAll(new ArrayList<>(map.values()));
    }
}
