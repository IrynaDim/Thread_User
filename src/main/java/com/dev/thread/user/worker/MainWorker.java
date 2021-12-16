package com.dev.thread.user.worker;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;

import com.dev.thread.user.thread.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class MainWorker {
    private final UserDaoJdbc userDaoJdbc;
    private final UserDaoMongo userDaoMongo;
    private Map<String, User> map;
    private Queue<String> dataFromFile;

    public MainWorker(UserDaoJdbc userDaoJdbc,
                      UserDaoMongo userDaoMongo) {
        this.userDaoJdbc = userDaoJdbc;
        this.userDaoMongo = userDaoMongo;
    }

    public void testThread(String version) throws InterruptedException {
        beforeThread();

        switch (version) {
            case "join":
            case "join array":
                startThreadJoin(version);
                break;
            case "count down":
                startThreadCountDown();
                break;
            case "executor":
                startThreadExecutor();
                break;
            case "executor2":
                startThreadExecutor2();
                break;
            case "executor3":
                startThreadExecutor3();
                break;
            case "executor4":
                startThreadExecutorCompletion();
                break;
            case "barrier":
                startThreadBarrier();
                break;
        }

        List<User> usersList = new ArrayList<>(map.values());

        userDaoJdbc.saveAll(usersList);
        userDaoMongo.saveAll(usersList);
    }


    private void startThreadJoin(String version) throws InterruptedException {
        ThreadUserJoin dataFromFileReading = new ThreadUserJoin(dataFromFile, map);
        Thread t = new Thread(dataFromFileReading);
        t.setName("Thread-One");
        Thread t1 = new Thread(dataFromFileReading);
        t1.setName("Thread-Two");

        t.start();
        t1.start();

        switch (version) {
            case "join":
                dataFromFileReading.completionJoin(t, t1);
            case "join array":
                dataFromFileReading.completionJoinArray(Arrays.asList(t, t1));
        }
    }

    private void startThreadCountDown() throws InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2); // два потока в пуле

        ThreadUserCountDown t = new ThreadUserCountDown(countDownLatch, dataFromFile, map);
        ThreadUserCountDown t1 = new ThreadUserCountDown(countDownLatch, dataFromFile, map);

        executor.submit(t);
        executor.submit(t1);

        countDownLatch.await();
    }

    private void startThreadExecutor() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future> futures = new ArrayList<>();
        futures.add(executorService.submit(new ThreadUserExecutor(dataFromFile, map, "Thread-Executor-2")));
        futures.add(executorService.submit(new ThreadUserExecutor(dataFromFile, map, "Thread-Executor-2")));
        for (Future future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void startThreadExecutor2() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ThreadUserExecutor t = new ThreadUserExecutor(dataFromFile, map, "Thread-Executor-1");
        ThreadUserExecutor t1 = new ThreadUserExecutor(dataFromFile, map, "Thread-Executor-2");
        executorService.submit(t);
        executorService.submit(t1);

        t.awaitTerminationAfterShutdown(executorService);
        t1.awaitTerminationAfterShutdown(executorService);

    }

    private void startThreadExecutor3() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ThreadUserExecutor t = new ThreadUserExecutor(dataFromFile, map, "Thread-Executor-1");
        ThreadUserExecutor t1 = new ThreadUserExecutor(dataFromFile, map, "Thread-Executor-2");
        executorService.execute(t);
        executorService.execute(t1);

        executorService.shutdown();

        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startThreadExecutorCompletion() {
        final ExecutorService pool = Executors.newFixedThreadPool(2);
        final CompletionService<Map<String, User>> service = new ExecutorCompletionService<>(pool);
        final List<? extends Callable<Map<String, User>>> callables = Arrays.asList(
                new ThreadUserCompletion(dataFromFile, map),
                new ThreadUserCompletion(dataFromFile, map)
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
    }

    private void startThreadBarrier() {
        CyclicBarrier barrier = new CyclicBarrier(2, () -> System.out.println("All tasks is done."));

        Thread t = new Thread(new ThreadUserCyclicBarrier(dataFromFile, map, barrier));
        Thread t1 = new Thread(new ThreadUserCyclicBarrier(dataFromFile, map, barrier));

        t.start();
        t1.start();

        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    private void beforeThread() {
        map = new HashMap<>();
        dataFromFile = FileReader.readFromFile("input.txt");
        userDaoJdbc.clearTable();
        userDaoMongo.clearTable();
    }
}
