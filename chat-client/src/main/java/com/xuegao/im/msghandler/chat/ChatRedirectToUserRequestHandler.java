package com.xuegao.im.msghandler.chat;

import com.xuegao.im.im.dispatcher.MessageHandler;
import com.xuegao.im.msg.chat.ChatRedirectToUserRequest;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChatRedirectToUserRequestHandler implements MessageHandler<ChatRedirectToUserRequest> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(Channel channel, ChatRedirectToUserRequest message) {
        logger.info("[execute][收到消息：{}]", message);
    }

    @Override
    public String getType() {
        return ChatRedirectToUserRequest.TYPE;
    }

}
