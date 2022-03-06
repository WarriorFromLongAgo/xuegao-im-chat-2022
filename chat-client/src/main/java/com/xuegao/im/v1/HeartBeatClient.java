package com.xuegao.im.v1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static com.sun.jmx.remote.internal.IIOPHelper.connect;

/**
 * @author xuegao
 * @version 1.0
 * @date 2022/3/6 19:56
 */
public class HeartBeatClient {
    private static final Logger log = LoggerFactory.getLogger(HeartBeatClient.class);

    int port;
    Channel channel;
    Random random;

    public HeartBeatClient(int port) {
        this.port = port;
        random = new Random();
    }

    public static void main(String[] args) {
        HeartBeatClient client = new HeartBeatClient(13000);
        client.start();
    }

    public void start() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("encoder", new StringEncoder());
                            pipeline.addLast(new HeartBeatClientHandler());
                        }
                    });

            connect(bootstrap, port);
            String text = "I am alive";
            while (channel.isActive()) {
                sendMsg(text);
            }
        } catch (Exception e) {
            log.info("[xuegao-im-chat-2022][HeartBeatClient][start][e]", e);
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void sendMsg(String text) throws Exception {
        channel.writeAndFlush(text);
        int num = random.nextInt(2);
        System.out.println(" client sent msg and sleep " + num);
        Thread.sleep(num * 1000);
    }

    static class HeartBeatClientHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) {
            System.out.println(" client received :" + msg);
            if (msg != null && msg.equals("you are out")) {
                System.out.println(" server closed connection , so client will close too");
                ctx.channel().closeFuture();
            }
        }
    }
}