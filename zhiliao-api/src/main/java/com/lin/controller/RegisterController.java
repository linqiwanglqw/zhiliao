package com.lin.controller;

import com.lin.common.aop.LogAnnotation;
import com.lin.vo.Result;
import com.lin.service.LoginService;
import com.lin.vo.params.LoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 注册
 */
@RestController
@RequestMapping("register")
public class RegisterController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    @LogAnnotation(module = "注册", operator = "注册接口")
    public Result register(@RequestBody LoginParam loginParam) {
        return loginService.register(loginParam);
    }
}
