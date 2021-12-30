package com.dev.thread.user.thread;

import com.dev.thread.user.model.User;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class ThreadUserJoin implements Runnable {
    private final Queue<String> dataFromFile;
    private final Map<String, User> map;
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public void run() {
        while (!dataFromFile.isEmpty()) {

            System.out.println(Thread.currentThread().getName());

            String string = dataFromFile.poll();
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
    }

    public void completionJoin(Thread t, Thread t1) throws InterruptedException {
        t.join();
        t1.join();
    }

    public void completionJoinArray(List<Thread> threads) throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }
    }
}
