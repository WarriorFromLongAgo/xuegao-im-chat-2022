package com.xuegao.im.netty.listener;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author xuegao
 * @version 1.0
 * @date 2022/6/4 16:19
 */
@Component
public class ServerStartListener11 implements CommandLineRunner, Ordered {

    @Override
    public int getOrder() {
        return 11;
    }

    @Override
    public void run(String... args) {
        System.out.println(getClass().getName() + "=============" + getOrder());
    }
}