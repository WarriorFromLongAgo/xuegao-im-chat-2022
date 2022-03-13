package com.xuegao.im.one.SimpleChannelInboundHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author xuegao
 * @version 1.0
 * @date 2022/3/13 16:02
 */
public class OneClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress("127.0.0.1", 1_0000))
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("encoder", new StringEncoder());
                            pipeline.addLast(new HeartBeatClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect().sync();
            Channel channel = channelFuture.channel();
            while (channelFuture.channel().isActive()) {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                channel.writeAndFlush("HeartBeat");
                int num = random.nextInt(2);
                System.out.println(" client sent HeartBeat, and sleep " + num);
                TimeUnit.SECONDS.sleep(num);
            }
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
        } finally {
            nioEventLoopGroup.shutdownGracefully().sync();
        }
    }
}

class HeartBeatClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 当被通知channel是活跃的时候，发送一条消息
        ctx.writeAndFlush("======================== client channelActive ========================");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 发生异常时，记录错误并关闭channel
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String hearBeat) throws Exception {
        // 记录已接收消息的转储
        System.out.println("Client received: " + hearBeat);
        if (!hearBeat.equals("HeartBeat")) {
            System.out.println(" server closed connection , so client will close too");
            ctx.channel().closeFuture();
        }
    }
}