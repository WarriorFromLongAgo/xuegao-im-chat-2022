package com.xuegao.im.common;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author xuegao
 * @version 1.0
 * @date 2022/3/20 21:33
 */
public class Executor {
    /**
     * 定义自己的线程池
     *
     * @return java.util.concurrent.Executor
     * @author fjm
     * @date 2022/3/20 21:23
     */
    @Bean("xuegaoImExecutorService")
    public java.util.concurrent.Executor xuegaoImExecutorService() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new VisiableThreadPoolTaskExecutor();
        //核心线程数
        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
        //最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(20);
        //配置队列大小
        threadPoolTaskExecutor.setQueueCapacity(1000);
        //配置线程池前缀
        threadPoolTaskExecutor.setThreadNamePrefix("xuegao-im-executor-");
        //拒绝策略
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}