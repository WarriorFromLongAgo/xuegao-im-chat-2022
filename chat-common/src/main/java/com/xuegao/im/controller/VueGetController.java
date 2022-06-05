package com.xuegao.im.controller;

import com.xuegao.im.utils.RespUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xuegao
 * @version 1.0
 * @date 2022/5/8 15:17
 */
@RestController
public class VueGetController {

    @GetMapping("/vue/get1")
    public RespUtil<Integer> get1() {
        return RespUtil.success(111111);
    }


}