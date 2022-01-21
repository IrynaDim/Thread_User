package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class ThreadExecutorAwait extends AbstractThread {
    public ThreadExecutorAwait(UserDaoJdbc userDaoJdbc,
                               UserDaoMongo userDaoMongo) {
        super(userDaoJdbc, userDaoMongo);
    }

    @Override
    public Map<String, User> run() {
        super.run();
        ExecutorService read = Executors.newFixedThreadPool(THREADS_NUMBER);
        read.execute(this::addToMap);
        read.execute(this::addToMap);
        awaitTerminationAfterShutdown(read);

        ExecutorService write = Executors.newFixedThreadPool(THREADS_NUMBER);
        write.execute(this::addToMongoDB);
        write.execute(this::addToMySQL);
        awaitTerminationAfterShutdown(write);
        return getMap();
    }

    public void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
