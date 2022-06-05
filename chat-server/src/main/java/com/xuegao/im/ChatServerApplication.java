package com.xuegao.im;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xuegao
 * @version 1.0
 * @date 2022/2/16 23:56
 */
@SpringBootApplication
public class ChatServerApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ChatServerApplication.class);
        application.run(args);
    }
}