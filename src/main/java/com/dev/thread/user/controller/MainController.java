package com.dev.thread.user.controller;

import com.dev.thread.user.worker.MainWorker;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/main")
public class MainController {
    private final MainWorker worker;

    public MainController(MainWorker worker) {
        this.worker = worker;
    }

    @PostMapping("/{version}")
    public void getRandom(
            @ApiParam(allowableValues = "join,join array,count down,executor")
            @PathVariable String version) throws InterruptedException {
        worker.testThread(version);
    }
}