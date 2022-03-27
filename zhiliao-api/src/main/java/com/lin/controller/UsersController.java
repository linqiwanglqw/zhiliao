package com.lin.controller;

import com.lin.service.SysUserService;
import com.lin.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UsersController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 获取用户信息
     * @param token：@RequestHeader("Authorization") String token获取头部token
     * @return
     */
    ///users/currentUser
    @GetMapping("currentUser")
    public Result currentUser(@RequestHeader("Authorization") String token){
        return sysUserService.findUserByToken(token);
    }
}
