package com.xuegao.im.v3_my;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author xuegao
 * @version 1.0
 * @date 2022/3/13 16:02
 */
public class HeartBeatServer {
    public static void main(String[] args) throws InterruptedException {
        // 创建 eventloopgroup
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            // 创建 server boot strap
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(eventLoopGroup)
                    // 指定所使用的NIO传输channel
                    .channel(NioServerSocketChannel.class)
                    // 使用指定的端口设置套接字地址
                    .localAddress(new InetSocketAddress(1_0000))
                    // 添加一个 echoServerHandler 到 子channel 的 ChannelPipeline
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // echoServerHandler 被标注 @ChannelHandler.Sharable ，所以我们总是可以使用同样的实例
                            // 这里对于所有的客户端连接来说，都会使用同一个EchoServerHandler，因为其被标注为@Sharable，
                            // 这将在后面的章节中讲到。——译者注
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            // 异步的绑定服务器，调用sync() 方法阻塞等待直到绑定完成
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            // 获取channel的closefuture，并阻塞当前线程知道他完成为止
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭 eventLoopGroup 释放所有的资源
            eventLoopGroup.shutdownGracefully().sync();
        }
    }
}

class EchoServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("channelRead0" + msg);

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("userEventTriggered");
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught");
        super.exceptionCaught(ctx, cause);
    }
}