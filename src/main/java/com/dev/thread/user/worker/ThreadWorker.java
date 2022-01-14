package com.dev.thread.user.worker;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import com.dev.thread.user.thread.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class ThreadWorker {
    private final UserDaoJdbc userDaoJdbc;
    private final UserDaoMongo userDaoMongo;

    public ThreadWorker(UserDaoJdbc userDaoJdbc, UserDaoMongo userDaoMongo) {
        this.userDaoJdbc = userDaoJdbc;
        this.userDaoMongo = userDaoMongo;
    }

    public List<User> chooseVersion(String version, String fileName) {
        switch (version) {
            case "join":
                return new ArrayList<>(startThreadJoin(fileName).values());
            case "count down":
                return new ArrayList<>(startThreadCountDown(fileName).values());
            case "executor":
                return new ArrayList<>(startThreadExecutor(fileName).values());
            case "executor2":
                return new ArrayList<>(startThreadExecutor2(fileName).values());
            case "completionService":
                return new ArrayList<>(startThreadCompletionService(fileName).values());
            case "barrier":
                return new ArrayList<>(startThreadBarrier(fileName).values());
            default:
                return null;
        }
    }

    private Map<String, User> startThreadJoin(String fileName) {
        long start = System.nanoTime();

        Map<String, User> map = new HashMap<>();
        Queue<String> dataFromFile = FileReader.readFromFile(fileName);

        ThreadUser threadUser = new ThreadUser(dataFromFile, map, userDaoJdbc, userDaoMongo);
        Thread t = new Thread(threadUser);
        Thread t1 = new Thread(threadUser);
        t.start();
        t1.start();
        try {
            threadUser.completionJoinArray(Arrays.asList(t, t1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.interrupt();
        t1.interrupt();

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Join: " + elapsed / 1000000);
        return map;
    }

    private Map<String, User> startThreadCountDown(String fileName) { // done
        long start = System.nanoTime();

        Map<String, User> map = new HashMap<>();
        Queue<String> dataFromFile = FileReader.readFromFile(fileName);

        CountDownLatch countDownLatch = new CountDownLatch(2);
        new ThreadUserCountDown(countDownLatch, dataFromFile, map, userDaoJdbc, userDaoMongo);
        new ThreadUserCountDown(countDownLatch, dataFromFile, map, userDaoJdbc, userDaoMongo);
        try {
            countDownLatch.await(); // ждет завершения всех потоков
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Count down: " + elapsed / 1000000);
        return map;
    }

    private Map<String, User> startThreadExecutor(String fileName) { // done
        long start = System.nanoTime();

        Map<String, User> map = new HashMap<>();
        Queue<String> dataFromFile = FileReader.readFromFile(fileName);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future> futures = new ArrayList<>();
        futures.add(executorService.submit(new ThreadUser(dataFromFile, map, userDaoJdbc, userDaoMongo)));
        futures.add(executorService.submit(new ThreadUser(dataFromFile, map, userDaoJdbc, userDaoMongo)));
        for (Future future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Executor: " + elapsed / 1000000);
        return map;
    }

    private Map<String, User> startThreadExecutor2(String fileName) {
        long start = System.nanoTime();
        Map<String, User> map = new HashMap<>();
        Queue<String> dataFromFile = FileReader.readFromFile(fileName);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ThreadUser t = new ThreadUser(dataFromFile, map, userDaoJdbc, userDaoMongo);
        ThreadUser t1 = new ThreadUser(dataFromFile, map, userDaoJdbc, userDaoMongo);
        executorService.submit(t);
        executorService.submit(t1);
        t.awaitTerminationAfterShutdown(executorService);
        t1.awaitTerminationAfterShutdown(executorService);

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Executor2: " + elapsed / 1000000);
        return map;
    }

    private Map<String, User> startThreadCompletionService(String fileName) {
        long start = System.nanoTime();

        Map<String, User> map = new HashMap<>();
        Queue<String> dataFromFile = FileReader.readFromFile(fileName);

        final ExecutorService pool = Executors.newFixedThreadPool(2);
        final CompletionService<Map<String, User>> service = new ExecutorCompletionService<>(pool);
        service.submit(new ThreadUserCompletion(dataFromFile, map, userDaoJdbc, userDaoMongo));
        service.submit(new ThreadUserCompletion(dataFromFile, map, userDaoJdbc, userDaoMongo));
        try {
            service.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pool.shutdown();

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("CompletionService: " + elapsed / 1000000);
        return map;
    }

    private Map<String, User> startThreadBarrier(String fileName) { // done
        long start = System.nanoTime();

        Map<String, User> map = new HashMap<>();
        Queue<String> dataFromFile = FileReader.readFromFile(fileName);

        CyclicBarrier barrier = new CyclicBarrier(2);
        new ThreadUserCyclicBarrier(barrier, dataFromFile, map, userDaoJdbc, userDaoMongo);
        new ThreadUserCyclicBarrier(barrier, dataFromFile, map, userDaoJdbc, userDaoMongo);
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Barrier: " + elapsed / 1000000);
        return map;
    }
}
