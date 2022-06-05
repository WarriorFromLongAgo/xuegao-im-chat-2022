package com.xuegao.im.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xuegao
 * @version 1.0
 * @date 2022/5/8 15:17
 */
@RestController
public class VuePostController {

    @PostMapping("/vue/post1")
    public JSONObject post1() {
        return JSON.parseObject("{\"code\":0,\"msg\":\"OK\",\"data\":\"xuegao-post1\"}");
    }
}