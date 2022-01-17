package com.dev.thread.user.worker;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;

import com.dev.thread.user.model.User;
import com.dev.thread.user.thread.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;


@Service
public class MainWorker {
    private final UserDaoJdbc userDaoJdbc;
    private final UserDaoMongo userDaoMongo;
    private final String FILE_NAME = "input.txt";
    private final Map<String, AbstractThread> commands = new HashMap<>();

    public MainWorker(UserDaoJdbc userDaoJdbc, UserDaoMongo userDaoMongo) {
        this.userDaoJdbc = userDaoJdbc;
        this.userDaoMongo = userDaoMongo;
        Queue<String> dataFromFile = FileReader.readFromFile(FILE_NAME);
        Map<String, User> map = new HashMap<>();
        commands.put("executor2", new ThreadExecutorAwait(dataFromFile, map, userDaoJdbc, userDaoMongo));
        commands.put("executor", new ThreadFuture(dataFromFile, map, userDaoJdbc, userDaoMongo));
        commands.put("join", new ThreadUser(dataFromFile, map, userDaoJdbc, userDaoMongo));
        commands.put("completionService", new ThreadUserCompletion(dataFromFile, map, userDaoJdbc, userDaoMongo));
        commands.put("count down", new ThreadUserCountDown(new CountDownLatch(2), dataFromFile, map, userDaoJdbc, userDaoMongo));
        commands.put("barrier", new ThreadUserCyclicBarrier(new CyclicBarrier(2), dataFromFile, map, userDaoJdbc, userDaoMongo));
    }

    public List<User> testThread(String version) {
        beforeThread();
        return new ArrayList<>(commands.get(version).startThread().values());
    }

    private void beforeThread() {
        userDaoJdbc.clearTable();
        userDaoMongo.clearTable();
    }
}
