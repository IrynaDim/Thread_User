package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import com.dev.thread.user.worker.FileReader;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

@EqualsAndHashCode(callSuper = true)
@Data
public class ThreadUserCountDown extends AbstractThread implements Runnable {
    private final CountDownLatch countDownLatch;

    public ThreadUserCountDown(CountDownLatch countDownLatch,
                               Queue<String> dataFromFile,
                               Map<String, User> map,
                               UserDaoJdbc userDaoJdbc,
                               UserDaoMongo userDaoMongo) {
        super(dataFromFile, map, userDaoJdbc, userDaoMongo);
        this.countDownLatch = countDownLatch;
        new Thread(this).start();
    }

    @Override
    public void run() {
        addToMap();
        countDownLatch.countDown();
        addToDb();
    }

    @Override
    public Map<String, User> startThread(String fileName) {
        long start = System.nanoTime();

        Map<String, User> map = new HashMap<>();
        Queue<String> dataFromFile = FileReader.readFromFile(fileName);

        CountDownLatch countDownLatch = new CountDownLatch(2);
        new ThreadUserCountDown(countDownLatch, dataFromFile, map, super.getUserDaoJdbc(), super.getUserDaoMongo());
        new ThreadUserCountDown(countDownLatch, dataFromFile, map, super.getUserDaoJdbc(), super.getUserDaoMongo());
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
}
