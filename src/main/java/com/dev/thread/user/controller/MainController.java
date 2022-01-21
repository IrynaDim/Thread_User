package com.dev.thread.user.controller;

import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.thread.*;
import com.dev.thread.user.worker.MainWorker;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/main")
public class MainController {
    private final MainWorker worker;
    private final ThreadExecutorAwait executorAwait;
    private final ThreadFuture threadFuture;
    private final ThreadJoin threadJoin;
    private final ThreadCompletion completion;
    private final ThreadCountDown countDown;
    private final ThreadCyclicBarrier cyclicBarrier;
    private final UserDaoJdbc userDaoJdbc;
    private final UserDaoMongo userDaoMongo;

    public MainController(MainWorker worker, ThreadExecutorAwait executorAwait, ThreadFuture threadFuture, ThreadJoin threadJoin, ThreadCompletion completion, ThreadCountDown countDown, ThreadCyclicBarrier cyclicBarrier, UserDaoJdbc userDaoJdbc, UserDaoMongo userDaoMongo) {
        this.worker = worker;
        this.executorAwait = executorAwait;
        this.threadFuture = threadFuture;
        this.threadJoin = threadJoin;
        this.completion = completion;
        this.countDown = countDown;
        this.cyclicBarrier = cyclicBarrier;
        this.userDaoJdbc = userDaoJdbc;
        this.userDaoMongo = userDaoMongo;
    }

    @PostMapping("/{version}")
    public void addUsers(
            @ApiParam(allowableValues = "join,count down,future,await,completionService,barrier")
            @PathVariable String version) {
        worker.testThread(version);
    }

    @GetMapping
    public Map<String, Long> avgTime() {
        List<AbstractThread> list = new ArrayList<>();
        Map<String, Long> result = new HashMap<>();
        fillList(list);
        long sum;
        for (AbstractThread thread : list) {
            sum = 0;
            for (int i = 0; i < 5; i++) {
                userDaoJdbc.clearTable();
                userDaoMongo.clearTable();
                long startTime = System.nanoTime();
                thread.run();
                long endTime = System.nanoTime();
                sum += (endTime - startTime) / 1000000;
            }
            result.put(thread.getClass().getSimpleName(), sum/5);
        }
        return result;
    }

    private void fillList(List<AbstractThread> list) {
        list.add(executorAwait);
        list.add(threadFuture);
        list.add(threadJoin);
        list.add(completion);
        list.add(countDown);
        list.add(cyclicBarrier);
    }
}
