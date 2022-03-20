package com.xuegao.im.msg.heartbeat;

import com.xuegao.im.im.dispatcher.Message;

/**
 * @author xuegao
 * @version 1.0
 * @date 2022/3/20 22:51
 */
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