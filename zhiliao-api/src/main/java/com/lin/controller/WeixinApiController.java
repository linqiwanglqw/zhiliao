package com.lin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lin.dao.pojo.SysUser;
import com.lin.service.SysUserService;
import com.lin.utils.ConstantPropertiesUtil;
import com.lin.utils.HttpClientUtils;
import com.lin.utils.JWTUtils;
import com.lin.vo.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/api/ucenter/wx")
public class WeixinApiController {

    @Autowired
    SysUserService sysUserService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    /**
     * 获取微信登录参数
     */
    @GetMapping("getLoginParam")
    @ResponseBody
    public Result genQrConnect(HttpSession session) throws UnsupportedEncodingException {
        String redirectUri = URLEncoder.encode(ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL, "UTF-8");
        Map<String, Object> map = new HashMap<>();
        map.put("appid", ConstantPropertiesUtil.WX_OPEN_APP_ID);
        map.put("redirect_uri", redirectUri);
        map.put("scope", "snsapi_login");
        map.put("state", System.currentTimeMillis()+"");//System.currentTimeMillis()+""
        return Result.success(map);
    }

    //微信扫描后回调的方法
    @GetMapping("callback")
    public String callback(String code,String state) {
        //第一步 获取临时票据 code
//        System.out.println("code:"+code);
        //第二步 拿着code和微信id和秘钥，请求微信固定地址 ，得到两个值
        //使用code和appid以及appscrect换取access_token
        //  %s   占位符
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);
        //使用httpclient请求这个地址
        try {
            String accesstokenInfo = HttpClientUtils.get(accessTokenUrl);
//            System.out.println("accesstokenInfo:"+accesstokenInfo);
            //从返回字符串获取两个值 openid  和  access_token
            JSONObject jsonObject = JSONObject.parseObject(accesstokenInfo);
            String access_token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");

            //判断数据库是否存在微信的扫描人信息
            //根据openid判断
            SysUser userInfo = sysUserService.findUserByOpenId(openid);
            if(userInfo == null) { //数据库不存在微信信息
                //第三步 拿着openid  和  access_token请求微信地址，得到扫描人信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
                String resultInfo = HttpClientUtils.get(userInfoUrl);
                System.out.println("resultInfo:"+resultInfo);
                JSONObject resultUserInfoJson = JSONObject.parseObject(resultInfo);
                //解析用户信息
                //用户昵称
                String nickname = resultUserInfoJson.getString("nickname");
                //用户头像
                String headimgurl = resultUserInfoJson.getString("headimgurl");
                //获取扫描人信息添加数据库
                userInfo = new SysUser();
                userInfo.setNickname(nickname);
                userInfo.setOpenid(openid);
                userInfo.setAccount(openid);
                userInfo.setAvatar(headimgurl);
                userInfo.setStatus("1");
                sysUserService.save(userInfo);
            }
            SysUser userInfoTwo = sysUserService.findUserByOpenId(openid);
            //生成token
            String token = JWTUtils.createToken(userInfoTwo.getId());
            //存入redis 1代表1天
            redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(userInfoTwo),1, TimeUnit.DAYS);

            //跳转到前端页面
            return "redirect:" + ConstantPropertiesUtil.YYGH_BASE_URL + "/#/callback?token="
                    +token+ "&openid="+userInfoTwo.getOpenid();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

