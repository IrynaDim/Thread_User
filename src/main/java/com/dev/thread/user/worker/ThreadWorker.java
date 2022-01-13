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
            case "join array":
                return new ArrayList<>(startThreadJoin(version, fileName).values());
            case "count down":
                return new ArrayList<>(startThreadCountDown(fileName).values());
            case "executor":
                return new ArrayList<>(startThreadExecutor(fileName).values());
            case "executor2":
                return new ArrayList<>(startThreadExecutor2(fileName).values());
            case "executor3":
                return new ArrayList<>(startThreadExecutor3(fileName).values());
            case "executor4":
                return new ArrayList<>(startThreadExecutorCompletion(fileName).values());
            case "barrier":
                return new ArrayList<>(startThreadBarrier(fileName).values());
            default:
                return null;
        }
    }

    private Map<String, User> startThreadJoin(String version, String fileName) {
        long start = System.nanoTime();

        Map<String, User> map = new HashMap<>();
        Queue<String> dataFromFile = FileReader.readFromFile(fileName);

        ThreadUserJoin dataFromFileReading = new ThreadUserJoin(dataFromFile, map, userDaoJdbc, userDaoMongo);
        Thread t = new Thread(dataFromFileReading);
        Thread t1 = new Thread(dataFromFileReading);

        t.start();
        t1.start();

        try {
            switch (version) {
                case "join":
                    dataFromFileReading.completionJoin(t, t1);
                case "join array":
                    dataFromFileReading.completionJoinArray(Arrays.asList(t, t1));
            }
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

    private Map<String, User> startThreadExecutor(String fileName) {
        long start = System.nanoTime();

        Map<String, User> map = new HashMap<>();
        Queue<String> dataFromFile = FileReader.readFromFile(fileName);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future> futures = new ArrayList<>();
        futures.add(executorService.submit(new ThreadUserExecutor(dataFromFile, map, userDaoJdbc, userDaoMongo)));
        futures.add(executorService.submit(new ThreadUserExecutor(dataFromFile, map, userDaoJdbc, userDaoMongo)));
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
        ThreadUserExecutor t = new ThreadUserExecutor(dataFromFile, map, userDaoJdbc, userDaoMongo);
        ThreadUserExecutor t1 = new ThreadUserExecutor(dataFromFile, map, userDaoJdbc, userDaoMongo);
        executorService.submit(t);
        executorService.submit(t1);

        t.awaitTerminationAfterShutdown(executorService);
        t1.awaitTerminationAfterShutdown(executorService);

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Executor2: " + elapsed / 1000000);
        return map;
    }

    private Map<String, User> startThreadExecutor3(String fileName) {
        long start = System.nanoTime();

        Map<String, User> map = new HashMap<>();
        Queue<String> dataFromFile = FileReader.readFromFile(fileName);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ThreadUserExecutor t = new ThreadUserExecutor(dataFromFile, map, userDaoJdbc, userDaoMongo);
        ThreadUserExecutor t1 = new ThreadUserExecutor(dataFromFile, map, userDaoJdbc, userDaoMongo);
        executorService.execute(t);
        executorService.execute(t1);

        executorService.shutdown();

        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Executor3: " + elapsed / 1000000);
        return map;
    }

    private Map<String, User> startThreadExecutorCompletion(String fileName) {
        long start = System.nanoTime();

        Map<String, User> map = new HashMap<>();
        Queue<String> dataFromFile = FileReader.readFromFile(fileName);

        final ExecutorService pool = Executors.newFixedThreadPool(2);
        final CompletionService<Map<String, User>> service = new ExecutorCompletionService<>(pool);
        final List<? extends Callable<Map<String, User>>> callables = Arrays.asList(
                new ThreadUserCompletion(dataFromFile, map, userDaoJdbc, userDaoMongo),
                new ThreadUserCompletion(dataFromFile, map, userDaoJdbc, userDaoMongo)
        );

        for (final Callable<Map<String, User>> callable : callables) {
            service.submit(callable);
        }

        pool.shutdown();

        try {
            while (!pool.isTerminated()) {
                service.take();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Executor4: " + elapsed / 1000000);
        return map;
    }

    private Map<String, User> startThreadBarrier(String fileName) { // done
        long start = System.nanoTime();

        Map<String, User> map = new HashMap<>();
        Queue<String> dataFromFile = FileReader.readFromFile(fileName);

        CyclicBarrier barrier = new CyclicBarrier(2);
        new ThreadUserCyclicBarrier(barrier, dataFromFile, map, userDaoJdbc, userDaoMongo);
        new ThreadUserCyclicBarrier(barrier, dataFromFile, map, userDaoJdbc, userDaoMongo);

        long finish = System.nanoTime();
        long elapsed = finish - start;
        System.out.println("Barrier: " + elapsed / 1000000);
        return map;
    }
}
