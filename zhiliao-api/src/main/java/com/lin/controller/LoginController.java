package com.lin.controller;

import com.lin.common.aop.LogAnnotation;
import com.lin.common.verificationCode.Captcha;
import com.lin.service.CaptchaService;
import com.lin.vo.CodeVo;
import com.lin.vo.Result;
import com.lin.service.LoginService;
import com.lin.vo.params.LoginParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping
    @ApiOperation("登录接口")
    @LogAnnotation(module = "登录", operator = "登录接口")
    public Result login(@RequestBody LoginParam loginParam) {
        //登录=》 验证用户 =》 访问用户表
        return loginService.login(loginParam);
    }

    @PostMapping("/checkCode")
    @ApiOperation("验证码")
    public Result checkCode(@RequestBody Captcha captcha) {
        return Result.success(captchaService.getCaptcha(captcha));
    }

    @PostMapping("/checkCheckCode")
    @ApiOperation("验证验证码")
    @LogAnnotation(module = "登录", operator = "验证验证码接口")
    public Result checkCheckCode(@RequestBody CodeVo codeVo) {
        String msg = captchaService.checkImageCode(codeVo.getNonceStr(),codeVo.getValue());
        if (StringUtils.isNotBlank(msg)) {
            return Result.fail(666,"验证失败");
        }else {
            String redisKey = "LOGINCOUNT::USER::LOGINNAME::" + codeVo.getAccount();
            stringRedisTemplate.delete(redisKey);
        }
        return Result.success("验证成功");
    }

}
