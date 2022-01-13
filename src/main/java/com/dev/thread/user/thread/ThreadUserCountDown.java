package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    }

    @Override
    public void run() {
        addToMap();
        addToDb();

        countDownLatch.countDown();
    }
}
