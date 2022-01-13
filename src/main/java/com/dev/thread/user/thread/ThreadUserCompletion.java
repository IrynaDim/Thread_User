package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ThreadUserCompletion extends AbstractThread implements Callable<Map<String, User>> {
    public ThreadUserCompletion(Queue<String> dataFromFile,
                                Map<String, User> map,
                                UserDaoJdbc userDaoJdbc,
                                UserDaoMongo userDaoMongo) {
        super(dataFromFile, map, userDaoJdbc, userDaoMongo);
    }

    @Override
    public Map<String, User> call() {
        addToMap();
        addToDb();
        return super.getMap();
    }
}
