package com.xuegao.im.msg.heartbeat;

import com.xuegao.im.im.dispatcher.Message;

public class HeartbeatRequest implements Message {

    /**
     * 类型 - 心跳请求
     */
    public static final String TYPE = "HEARTBEAT_REQUEST";

    @Override
    public String toString() {
        return "HeartbeatRequest{}";
    }

}