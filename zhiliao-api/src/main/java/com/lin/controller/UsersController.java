package com.lin.controller;

import com.lin.dao.pojo.SysUser;
import com.lin.service.SysUserService;
import com.lin.vo.Result;
import com.lin.vo.params.UserPatam;
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

    /**
     * 根据账户查找用户
     * @return
     */
    @GetMapping("findUserByAccount/{account}")
    public Result findUserByAccount(@PathVariable("account") String account){
        SysUser userByAccount = sysUserService.findUserByAccount(account);
        userByAccount.setPassword(null);
        return Result.success(userByAccount);
    }

    /**
     * 修改用户信息
     */
    @PutMapping ("updateUser")
    public Result updateUser(@RequestBody UserPatam userPatam){
       return sysUserService.updateUser(userPatam);
    }

}
