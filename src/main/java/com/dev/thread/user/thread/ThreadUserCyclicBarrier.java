package com.dev.thread.user.thread;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

@Data
@AllArgsConstructor
public class ThreadUserCyclicBarrier implements Runnable {
    private final Queue<String> dataFromFile;
    private final Map<String, User> map;
    private CyclicBarrier barrier;
    private final UserDaoJdbc userDaoJdbc;
    private final UserDaoMongo userDaoMongo;

    @Override
    public void run() {
        try {
            while (!dataFromFile.isEmpty()) {
                String string;
                synchronized (dataFromFile) {
                    string = dataFromFile.poll();
                }
                if (string != null) {
                    String[] strings = string.split(",");
                    String name = strings[1];
                    Double sum = Double.valueOf(strings[2]);
                    User user = new User();
                    user.setName(name);
                    user.setSum(sum);

                    synchronized (map) {
                        map.put(name, map.containsKey(name)
                                ? new User(null, name, map.get(name).getSum() + sum) : user);
                    }
                }
            }
            userDaoJdbc.saveAll(new ArrayList<>(map.values()));
            userDaoMongo.saveAll(new ArrayList<>(map.values()));
            System.out.println(Thread.currentThread().getName()
                    + " waiting for others to reach barrier.");
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
