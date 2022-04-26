package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class ThreadCompletion extends AbstractThread {
    public ThreadCompletion(UserDaoJdbc userDaoJdbc,
                            UserDaoMongo userDaoMongo) {
        super(userDaoJdbc, userDaoMongo);
    }

    @Override
    public Map<String, User> run() {
        super.run();
        ExecutorService executorService = Executors.newFixedThreadPool(THREADS_NUMBER);
        CompletionService<Map<String, User>> service = new ExecutorCompletionService<>(executorService);
        service.submit(this::addToMap, null);
        service.submit(this::addToMap, null);
        try {
            service.take();
            service.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        service.submit(this::addToMongoDB, null);
        service.submit(this::addToMySQL, null);
        try {
            service.take();
            service.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
        return getMap();
    }
}
