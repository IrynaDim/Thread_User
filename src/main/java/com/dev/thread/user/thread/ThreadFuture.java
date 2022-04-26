package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class ThreadFuture extends AbstractThread {
    public ThreadFuture(UserDaoJdbc userDaoJdbc,
                      UserDaoMongo userDaoMongo) {
        super(userDaoJdbc, userDaoMongo);
    }

    @Override
    public Map<String, User> run() {
        super.run();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future> futuresMap = new ArrayList<>();
        futuresMap.add(executorService.submit(this::addToMap));
        futuresMap.add(executorService.submit(this::addToMap));
        for (Future future : futuresMap) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        List<Future> futuresDB = new ArrayList<>();
        futuresDB.add(executorService.submit(this::addToMongoDB));
        futuresDB.add(executorService.submit(this::addToMySQL));
        for (Future future : futuresDB) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return getMap();
    }
}
