package com.xuegao.im.v2;

import com.xuegao.im.Constants;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * <p>
 * 用于捕获{@link IdleState#WRITER_IDLE}事件（未在指定时间内向服务器发送数据），
 * 然后向<code>Server</code>端发送一个心跳包。
 * </p>
 * 类ClientIdleStateTrigger也是一个Handler，只是重写了userEventTriggered方法，
 * 用于捕获IdleState.WRITER_IDLE事件（未在指定时间内向服务器发送数据），然后向Server端发送一个心跳包。
 * <p>
 * 心跳触发器：
 */
public class ClientIdleStateTrigger extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                // write heartbeat to server
                ctx.writeAndFlush(Constants.HEART_BEAT.HEART_BEAT);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}