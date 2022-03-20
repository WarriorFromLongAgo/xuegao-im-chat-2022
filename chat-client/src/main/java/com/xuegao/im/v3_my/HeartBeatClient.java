package com.xuegao.im.v3_my;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author xuegao
 * @version 1.0
 * @date 2022/3/13 16:02
 */
public class HeartBeatClient {
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
                            pipeline.addLast(new IdleStateHandler(
                                    0, 4, 0));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect().sync();
            Channel channel = channelFuture.channel();
            // v1
            newThreadSendHeatBeatClient(channel);
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
        } finally {
            nioEventLoopGroup.shutdownGracefully().sync();
        }
    }

    private static void newThreadSendHeatBeatClient(Channel channel) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (channel.isActive()) {
                    ThreadLocalRandom random = ThreadLocalRandom.current();
                    channel.writeAndFlush("ping");
                    int num = random.nextInt(6);
                    System.out.println(" client sent HeartBeat, and sleep " + num);
                    try {
                        TimeUnit.SECONDS.sleep(num);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}

class HeartBeatClientHandler extends SimpleChannelInboundHandler<String> {
    private static final ByteBuf HEARTBEAT_SEQUENCE =
            Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("ping", CharsetUtil.UTF_8));

    private static final int TRY_TIMES = 3;

    private int currentTime = 0;

    private static String SEND_SUCCESS = "发送成功";

    private static String SEND_FAIL = "发送失败";

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 当被通知channel是活跃的时候，发送一条消息
        ctx.writeAndFlush("======================== HeartBeatClientHandler channelActive ========================");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.writeAndFlush("======================== HeartBeatClientHandler channelInactive ========================");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Client exceptionCaught: " + cause.getMessage());
        // 发生异常时，记录错误并关闭channel
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String hearBeat) throws Exception {
        System.out.println(" server closed connection , so client will close too");
        ctx.channel().closeFuture();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("客户端循环心跳监测发送: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                if (currentTime <= TRY_TIMES) {
                    System.out.println("currentTime:" + currentTime);
                    System.out.println("ping writer_idle");
                    currentTime++;
                    ctx.channel().writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
                }
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Client 收到消息: " + msg);
        String info = (String) msg;
        ByteBuf byteBuf = Unpooled.copiedBuffer(("收到消息 = " + info).getBytes());
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
    }
}