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
//            case "semaphore":
//                startThreadSemaphore();  // -
//                break;
//            case "cyclic barrier": // -
//                cycleBarrier();
//                break;

            default:
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

    private void startThreadExecutor() { // +
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Future> futures = new ArrayList<>();
        futures.add(executorService.submit(new ThreadExecutor2(dataFromFile, map, "Thread-Executor-2")));
        futures.add(executorService.submit(new ThreadExecutor2(dataFromFile, map, "Thread-Executor-2")));
        for (Future future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
    }


//    private void cycleBarrier() {
//        CyclicBarrier barrier = new CyclicBarrier(2, () -> System.out.println("clean up job after all tasks are done."));
//
//        Thread t = new Thread(new ThreadUserCyclicBarrier(dataFromFile, map, barrier));
//        Thread t1 = new Thread(new ThreadUserCyclicBarrier(dataFromFile, map, barrier));
//
//        t.start();
//        t1.start();
//
//
//    }
//    private void startThreadSemaphore() {
//        //   ExecutorService executorService = Executors.newFixedThreadPool(2);
//        Semaphore sem = new Semaphore(2);
//        Thread t = new Thread(new ThreadUserSemaphore(dataFromFile, map, sem, "Thread-Semaphore-1"));
//        Thread t1 = new Thread(new ThreadUserSemaphore(dataFromFile, map, sem, "Thread-Semaphore-2"));
//
//        t.start();
//        t1.start();
//
//    }

    private void beforeThread() {
        map = new HashMap<>();
        dataFromFile = FileReader.readFromFile("input.txt");
        userDaoJdbc.clearTable();
        userDaoMongo.clearTable();
    }
}

//коллбек