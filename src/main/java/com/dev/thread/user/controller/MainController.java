package com.dev.thread.user.controller;

import com.dev.thread.user.worker.MainWorker;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/main")
public class MainController {
    private final MainWorker worker;

    public MainController(MainWorker worker) {
        this.worker = worker;
    }

    @PostMapping("/{version}")
    public void getRandom(@PathVariable String version) throws InterruptedException, SQLException {
        worker.testThread(version);
    }
}
