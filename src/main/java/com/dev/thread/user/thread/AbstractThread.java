package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import com.dev.thread.user.util.FileReader;
import lombok.Data;

import java.util.*;

@Data
public abstract class AbstractThread implements ThreadService {
    private Queue<String> dataFromFile;
    private final Map<String, User> map = new HashMap<>();
    private UserDaoJdbc userDaoJdbc;
    private UserDaoMongo userDaoMongo;
    public static final int THREADS_NUMBER = 2;
    public static final String FILE_NAME = "input.txt";

    public AbstractThread(UserDaoJdbc userDaoJdbc, UserDaoMongo userDaoMongo) {
        this.userDaoJdbc = userDaoJdbc;
        this.userDaoMongo = userDaoMongo;
    }

    @Override
    public Map<String, User> run() {
        dataFromFile = FileReader.readFromFile(FILE_NAME);
        map.clear();
        return null;
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

    public void addToMongoDB() {
        userDaoMongo.saveAll(new ArrayList<>(map.values()));
    }

    public void addToMySQL() {
        userDaoJdbc.saveAll(new ArrayList<>(map.values()));
    }
}
