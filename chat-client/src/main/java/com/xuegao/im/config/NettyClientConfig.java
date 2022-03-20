package com.xuegao.im.config;

import com.xuegao.im.im.dispatcher.MessageDispatcher;
import com.xuegao.im.im.dispatcher.MessageHandlerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xuegao
 * @version 1.0
 * @date 2022/3/20 21:55
 */
@Configuration
public class NettyClientConfig {

    @Bean
    public MessageDispatcher messageDispatcher() {
        return new MessageDispatcher();
    }

    @Bean
    public MessageHandlerContainer messageHandlerContainer() {
        return new MessageHandlerContainer();
    }

}