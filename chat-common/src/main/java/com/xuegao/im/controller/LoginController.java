package com.xuegao.im.controller;

import com.xuegao.im.utils.RespUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xuegao
 * @version 1.0
 * @date 2022/6/1 18:06
 */
@RestController
public class LoginController {

    @GetMapping("/im/login")
    public RespUtil<Integer> get1() {
        return RespUtil.success(111111);
    }

}