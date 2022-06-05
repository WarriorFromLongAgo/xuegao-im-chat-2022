package com.xuegao.im.netty;

import com.xuegao.im.netty.ws2.WsChannelInitializer2;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author xuegao
 * @version 1.0
 * @date 2022/6/4 16:10
 */
@Configuration
@Component
public class NettyServer {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private Channel serverChannel;

    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup();
    }

    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup();
    }

    @Bean(name = "serverBootstrap")
    public ServerBootstrap bootstrap() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new HttpServerCodec());
                        // pipeline.addLast(new WsChannelInitializer());
                        pipeline.addLast(new WsChannelInitializer2());

                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        return serverBootstrap;
    }

    public void start(int port) throws Exception {
        log.info("[xuegao-im-chat-2022][NettyServer][start][port={}]", port);
        serverChannel = bootstrap().bind(port)
                .sync()
                .channel();
        log.info("[xuegao-im-chat-2022][NettyServer][start完成]");
    }

    public void stop() {
        log.info("[xuegao-im-chat-2022][NettyServer][stop]");
        if (Objects.nonNull(serverChannel)) {
            serverChannel.close();
            log.info("[xuegao-im-chat-2022][NettyServer][stop][serverChannel.close()]");
        }
        if (Objects.nonNull(serverChannel) && Objects.nonNull(serverChannel.parent())) {
            serverChannel.parent().close();
            log.info("[xuegao-im-chat-2022][NettyServer][stop][serverChannel.parent().close()]");
        }
        log.info("[xuegao-im-chat-2022][NettyServer][stop完成]");
    }

}