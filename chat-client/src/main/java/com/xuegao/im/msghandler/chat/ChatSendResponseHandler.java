package com.xuegao.im.msghandler.chat;

import com.xuegao.im.im.dispatcher.MessageHandler;
import com.xuegao.im.msg.chat.ChatSendResponse;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChatSendResponseHandler implements MessageHandler<ChatSendResponse> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(Channel channel, ChatSendResponse message) {
        logger.info("[execute][发送结果：{}]", message);
    }

    @Override
    public String getType() {
        return ChatSendResponse.TYPE;
    }

}
