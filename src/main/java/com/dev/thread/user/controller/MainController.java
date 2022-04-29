package com.dev.thread.user.controller;

import com.dev.thread.user.worker.MainWorker;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/main")
public class MainController {
    @PostMapping("/{version}")
    public void addUsers() {

    }
}
