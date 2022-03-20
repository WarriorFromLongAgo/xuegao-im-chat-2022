package com.xuegao.im.v3_my;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GenericFutureListener;

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
                            // v1
                            ch.pipeline().addLast(new IdleStateHandler(
                                    5, 0, 0));
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
    private int loss_connect_time = 0;
    private static byte[] HEART_BEAT_MSG = "pong".getBytes();

    private static String SEND_SUCCESS = "发送心跳成功";

    private static String SEND_FAIL = "发生心跳失败";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
        super.channelActive(ctx);
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        System.out.println("acceptInboundMessage 1 = " + msg);
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            System.out.println("acceptInboundMessage 2 = " + byteBuf);
            System.out.println("acceptInboundMessage 3 = " + byteBuf.toString(CharsetUtil.UTF_8));
        }
        return super.acceptInboundMessage(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead 1 = " + msg);
        if (msg instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf) msg;
            System.out.println("channelRead 2 = " + byteBuf);
            System.out.println("channelRead 3 = " + byteBuf.toString(CharsetUtil.UTF_8));
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("channelRegistered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        System.out.println("channelUnregistered");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("channelInactive");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        System.out.println("channelReadComplete");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
        System.out.println("channelWritabilityChanged");
    }

    @Override
    protected void ensureNotSharable() {
        super.ensureNotSharable();
        System.out.println("ensureNotSharable");
    }

    @Override
    public boolean isSharable() {
        System.out.println("isSharable");
        return super.isSharable();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        System.out.println("handlerAdded");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        System.out.println("handlerRemoved");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught");
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("channelRead0 = " + msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("userEventTriggered = " + evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ByteBuf byteBuf = Unpooled.copiedBuffer(HEART_BEAT_MSG);
                ctx.channel().writeAndFlush(byteBuf).addListener(new GenericFutureListener<ChannelPromise>() {
                    @Override
                    public void operationComplete(ChannelPromise future) throws Exception {
                        boolean isDone = future.isDone();
                        if (!isDone) {
                            System.out.println(SEND_FAIL);
                        } else {
                            System.out.println(SEND_SUCCESS);
                        }
                    }
                });

                loss_connect_time++;
                System.out.println("5 秒没有接收到客户端的信息了");
                if (loss_connect_time > 2) {
                    System.out.println("关闭这个不活跃的channel");
                    ctx.channel().close();
                }
            }
            if (event.state() == IdleState.WRITER_IDLE) {

            }
            if (event.state() == IdleState.ALL_IDLE) {

            }
        } else {
            System.out.println("super.userEventTriggered(ctx, evt);");
            super.userEventTriggered(ctx, evt);
        }
    }
}