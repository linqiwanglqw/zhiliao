package com.lin.controller;

import com.lin.common.aop.LogAnnotation;
import com.lin.vo.Result;
import com.lin.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("logout")
public class LogoutController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    @LogAnnotation(module = "退出", operator = "登出接口")
    public Result logout(@RequestHeader("Authorization") String token) {
        return loginService.logout(token);
    }
}
