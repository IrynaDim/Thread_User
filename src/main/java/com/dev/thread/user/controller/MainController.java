package com.dev.thread.user.controller;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/main")
public class MainController {

    @GetMapping()
    public String test() {
        return "Version 1";
    }
}
