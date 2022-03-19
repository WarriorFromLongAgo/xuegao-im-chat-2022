package com.xuegao.im.one.ChannelInboundHandlerAdapter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author xuegao
 * @version 1.0
 * @date 2022/3/13 16:02
 */
public class OneServer {
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

// EchoServerHandler
// isSharable
// handlerAdded
// channelRegistered
// channelActive
// 23:47:17.455 [nioEventLoopGroup-2-2] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.maxCapacityPerThread: 4096
// 23:47:17.455 [nioEventLoopGroup-2-2] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.ratio: 8
// 23:47:17.455 [nioEventLoopGroup-2-2] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.chunkSize: 32
// 23:47:17.461 [nioEventLoopGroup-2-2] DEBUG io.netty.buffer.AbstractByteBuf - -Dio.netty.buffer.checkAccessible: true
// 23:47:17.461 [nioEventLoopGroup-2-2] DEBUG io.netty.buffer.AbstractByteBuf - -Dio.netty.buffer.checkBounds: true
// 23:47:17.462 [nioEventLoopGroup-2-2] DEBUG io.netty.util.ResourceLeakDetectorFactory - Loaded default ResourceLeakDetector: io.netty.util.ResourceLeakDetector@160ca810
// channelRead
// 23:47:17.465 [nioEventLoopGroup-2-2] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded inbound message PooledUnsafeDirectByteBuf(ridx: 0, widx: 79, cap: 2048) that reached at the tail of the pipeline. Please check your pipeline configuration.
// 23:47:17.471 [nioEventLoopGroup-2-2] DEBUG io.netty.channel.DefaultChannelPipeline - Discarded message pipeline : [EchoServerHandler#0, DefaultChannelPipeline$TailContext#0]. Channel : [id: 0x694fa93c, L:/127.0.0.1:10000 - R:/127.0.0.1:57481].
// channelReadComplete

// exceptionCaught
// channelInactive
// channelUnregistered
// handlerRemoved

// channelRead表示接收消息，可以看到msg转换成了ByteBuf，
// 然后打印，也就是把Client传过来的消息打印了一下，你会发现每次打印完后，
// channelReadComplete也会调用，如果你试着传一个超长的字符串过来，超过1024个字母长度，
// 你会发现channelRead会调用多次，而channelReadComplete只调用一次。
// 所以这就比较清晰了吧，因为ByteBuf是有长度限制的，所以超长了，就会多次读取，也就是调用多次channelRead，
// 而channelReadComplete则是每条消息只会调用一次，无论你多长，分多少次读取，只在该条消息最后一次读取完成的时候调用，
// 所以这段代码把关闭Channel的操作放在channelReadComplete里，放到channelRead里可能消息太长了，
// 结果第一次读完就关掉连接了，后面的消息全丢了。

class EchoServerHandler extends ChannelInboundHandlerAdapter {
    public EchoServerHandler() {
        super();
        System.out.println("EchoServerHandler");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("channelRead");
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelRegistered");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelUnregistered");
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelInactive");
        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelReadComplete");
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelWritabilityChanged");
        super.channelWritabilityChanged(ctx);
    }

    @Override
    protected void ensureNotSharable() {
        System.out.println("ensureNotSharable");
        super.ensureNotSharable();
    }

    @Override
    public boolean isSharable() {
        System.out.println("isSharable");
        return super.isSharable();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded");
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved");
        super.handlerRemoved(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
        super.channelActive(ctx);
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