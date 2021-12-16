package com.dev.thread.user.thread;

import com.dev.thread.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;

@Data
@AllArgsConstructor
public class ThreadUserCountDown extends Thread {
    private final CountDownLatch countDownLatch;
    private final Queue<String> dataFromFile;
    private final Map<String, User> map;

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
        countDownLatch.countDown();
    }
}
