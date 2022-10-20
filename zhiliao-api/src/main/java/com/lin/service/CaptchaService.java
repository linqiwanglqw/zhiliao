package com.lin.service;

import com.lin.common.verificationCode.Captcha;


public interface CaptchaService {

    String checkImageCode(String imageKey, String imageCode);

    Object getCaptcha(Captcha captcha);

}
