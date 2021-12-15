package com.dev.thread.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableSwagger2
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}

// TODO
// 2. make connections to the all dbs
// 3. work with file. read it using thread
// read file and put it in Que
// read it with 2 threads
// add lombok
// add @Value
// переход сразу на localhost8080