package com.dev.thread.user.worker;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;

import com.dev.thread.user.model.User;
import com.dev.thread.user.thread.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MainWorker {
    private final UserDaoJdbc userDaoJdbc;
    private final UserDaoMongo userDaoMongo;
    private final Map<String, AbstractThread> commands = new HashMap<>();

    public MainWorker(UserDaoJdbc userDaoJdbc,
                      UserDaoMongo userDaoMongo,
                      ThreadExecutorAwait executorAwait,
                      ThreadFuture threadFuture,
                      ThreadJoin threadJoin,
                      ThreadCompletion completion,
                      ThreadCountDown countDown,
                      ThreadCyclicBarrier cyclicBarrier) {
        this.userDaoJdbc = userDaoJdbc;
        this.userDaoMongo = userDaoMongo;

        commands.put("await", executorAwait);
        commands.put("future", threadFuture);
        commands.put("join", threadJoin);
        commands.put("completionService", completion);
        commands.put("count down", countDown);
        commands.put("barrier", cyclicBarrier);
    }

    public List<User> testThread(String version) {
        beforeThread();
        return new ArrayList<>(commands.get(version).run().values());
    }

    private void beforeThread() {
        userDaoJdbc.clearTable();
        userDaoMongo.clearTable();
    }
}
