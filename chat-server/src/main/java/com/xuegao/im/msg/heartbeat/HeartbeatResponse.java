package com.xuegao.im.msg.heartbeat;

import com.xuegao.im.im.dispatcher.Message;

/**
 * @author xuegao
 * @version 1.0
 * @date 2022/3/20 22:51
 */
public class HeartbeatResponse implements Message {

    /**
     * 类型 - 心跳响应
     */
    public static final String TYPE = "HEARTBEAT_RESPONSE";

    @Override
    public String toString() {
        return "HeartbeatResponse{}";
    }

}