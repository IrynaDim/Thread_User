package com.dev.thread.user.util;

import com.dev.thread.user.model.User;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ThreadUser implements Runnable {
    private final Queue<String> dataFromFile;
    private final Map<String, User> map;

    public ThreadUser(Queue<String> dataFromFile, Map<String, User> map) {
        this.dataFromFile = dataFromFile;
        this.map = map;
    }

    @Override
    public void run() {
        while (!dataFromFile.isEmpty()) {

            System.out.println(Thread.currentThread().getName());

            String string;
            synchronized (dataFromFile) {
                string = dataFromFile.peek();
                dataFromFile.remove(string);
            }
            List<String> data = (List.of(string.split(",")));
            User user = new User();
            user.setName(data.get(1));
            user.setSum(Double.valueOf(data.get(2)));
            synchronized (map) {
                User userFromMap = map.get(user.getName());
                if (userFromMap == null) {
                    map.put(user.getName(), user);
                } else {
                    userFromMap.setSum(userFromMap.getSum() + user.getSum());
                    map.put(userFromMap.getName(), userFromMap);
                }
            }
        }
    }
}
