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
        long start = System.nanoTime();

        super.run();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CompletionService<Map<String, User>> readService = new ExecutorCompletionService<>(executorService);
        readService.submit(this::add);
        readService.submit(this::add);
        try {
            readService.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        CompletionService<Map<String, User>> writeService = new ExecutorCompletionService<>(executorService);
        writeService.submit(this::addDB1);
        writeService.submit(this::addDB2);
        try {
            writeService.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("CompletionService.take(): " + elapsed / 1000000);
        return getMap();
    }

    public Map<String, User> add() {
        this.addToMap();
        return null;
    }

    public Map<String, User> addDB1() {
        this.addToMongoDB();
        return null;
    }

    public Map<String, User> addDB2() {
        this.addToMySQL();
        return null;
    }
}
