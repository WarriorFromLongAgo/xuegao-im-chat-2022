package com.xuegao.im.msghandler.heartbeat;

import com.xuegao.im.im.codec.Invocation;
import com.xuegao.im.im.dispatcher.MessageHandler;
import com.xuegao.im.msg.heartbeat.HeartbeatRequest;
import com.xuegao.im.msg.heartbeat.HeartbeatResponse;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author xuegao
 * @version 1.0
 * @date 2022/3/20 22:51
 */
@Component
public class HeartbeatRequestHandler implements MessageHandler<HeartbeatRequest> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(Channel channel, HeartbeatRequest message) {
        logger.info("[execute][收到连接({}) 的心跳请求]", channel.id());
        // 响应心跳
        HeartbeatResponse response = new HeartbeatResponse();
        channel.writeAndFlush(new Invocation(HeartbeatResponse.TYPE, response));
    }

    @Override
    public String getType() {
        return HeartbeatRequest.TYPE;
    }

}