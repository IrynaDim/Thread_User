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

        return map;
    }

    private Map<String, User> startThreadCountDown(String fileName) {
        Map<String, User> map = new HashMap<>();
        Queue<String> dataFromFile = FileReader.readFromFile(fileName);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2); // два потока в пуле

        ThreadUserCountDown t = new ThreadUserCountDown(countDownLatch, dataFromFile, map, userDaoJdbc, userDaoMongo);
        ThreadUserCountDown t1 = new ThreadUserCountDown(countDownLatch, dataFromFile, map, userDaoJdbc, userDaoMongo);

        executor.submit(t);
        executor.submit(t1);

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return map;
    }

    private Map<String, User> startThreadExecutor(String fileName) {
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

        return map;
    }

    private Map<String, User> startThreadExecutor2(String fileName) {
        Map<String, User> map = new HashMap<>();
        Queue<String> dataFromFile = FileReader.readFromFile(fileName);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ThreadUserExecutor t = new ThreadUserExecutor(dataFromFile, map, userDaoJdbc, userDaoMongo);
        ThreadUserExecutor t1 = new ThreadUserExecutor(dataFromFile, map, userDaoJdbc, userDaoMongo);
        executorService.submit(t);
        executorService.submit(t1);

        t.awaitTerminationAfterShutdown(executorService);
        t1.awaitTerminationAfterShutdown(executorService);

        return map;
    }

    private Map<String, User> startThreadExecutor3(String fileName) {
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
        return map;
    }

    private Map<String, User> startThreadExecutorCompletion(String fileName) {
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
                final Future<Map<String, User>> future = service.take();
                System.out.println("Thread is stop");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return map;
    }

    private Map<String, User> startThreadBarrier(String fileName) {
        Map<String, User> map = new HashMap<>();
        Queue<String> dataFromFile = FileReader.readFromFile(fileName);

        CyclicBarrier barrier = new CyclicBarrier(2, () -> System.out.println("All tasks is done."));

        Thread t = new Thread(new ThreadUserCyclicBarrier(barrier, dataFromFile, map, userDaoJdbc, userDaoMongo));
        Thread t1 = new Thread(new ThreadUserCyclicBarrier(barrier, dataFromFile, map, userDaoJdbc, userDaoMongo));

        t.start();
        t1.start();

        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        return map;
    }
}
