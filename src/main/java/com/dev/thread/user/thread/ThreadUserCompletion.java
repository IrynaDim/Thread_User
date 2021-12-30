package com.dev.thread.user.thread;

import com.dev.thread.user.model.User;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class ThreadUserCompletion implements Callable<Map<String, User>> {
    private final Queue<String> dataFromFile;
    private final Map<String, User> map;
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public Map<String, User> call() {
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
        return map;
    }
}

