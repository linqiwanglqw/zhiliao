package com.lin.service.impl;

import com.alibaba.fastjson.JSON;
import com.lin.dao.pojo.SysUser;
import com.lin.service.LoginService;
import com.lin.service.SysUserService;
import com.lin.utils.JWTUtils;
import com.lin.vo.ErrorCode;
import com.lin.vo.Result;
import com.lin.vo.params.LoginParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
//事务注解
@Transactional
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String slat = "lqw!@#！lsf";

    @Override
    public Result login(LoginParam loginParam) {
        /**
         * 1. 检查参数是否合法
         * 2. 根据用户名和密码去user表中查询 是否存在
         * 3. 如果不存在 登录失败
         * 4. 如果存在 ，使用jwt 生成token 返回给前端
         * 5. token放入redis当中，redis  token：user信息 设置过期时间
         *  (登录认证的时候 先认证token字符串是否合法，去redis认证是否存在)
         */
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        //判断是否为空
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        password = DigestUtils.md5Hex(password + slat);
        //去数据库中查
        SysUser sysUser = sysUserService.findUser(account, password);
        //检测
        Result codeResult = ackCode(account);
        if(codeResult != null && sysUser==null){
          return codeResult;
        } else if (sysUser == null ) {
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(), ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        } else if (sysUser.getDeleted() == 1) {
            return Result.fail(ErrorCode.ACCOUNT_DELETE.getCode(), ErrorCode.ACCOUNT_DELETE.getMsg());
        } else if (Objects.equals(sysUser.getStatus(), "0")) {
            return Result.fail(ErrorCode.ACCOUNT_BLOCKED.getCode(), ErrorCode.ACCOUNT_BLOCKED.getMsg());
        }
        //修改登录时间
        SysUser sysUserUdpate = new SysUser();
        sysUserUdpate.setNickname(sysUser.getNickname());
        sysUserUdpate.setId(sysUser.getId());
        sysUserUdpate.setAccount(account);
        sysUserUdpate.setLastLogin(System.currentTimeMillis());
        this.sysUserService.updateLastlogin(sysUserUdpate);
        //生成token
        String token = JWTUtils.createToken(sysUser.getId());
        //存入redis 7代表7天
        redisTemplate.opsForValue().set("TOKEN_" + token, JSON.toJSONString(sysUser), 7, TimeUnit.DAYS);
        return Result.success(token);
    }

    /**
     * 防止暴力破解
     * @param account
     * @return
     */
    public Result ackCode(String account){
        //统计尝试的登录记录
        //该记录不会被验证码删除
        String redisKeySum = "LOGINSUMCOUNT::USER::LOGINNAME::" + account;
        stringRedisTemplate.boundValueOps(redisKeySum).increment(1);
        stringRedisTemplate.expire(redisKeySum,100 , TimeUnit.MINUTES);
        String numCont = stringRedisTemplate.opsForValue().get(redisKeySum);
        //对登录次数做记录 防止暴力登录
        String redisKey = "LOGINCOUNT::USER::LOGINNAME::" + account;
        stringRedisTemplate.boundValueOps(redisKey).increment(1);
        stringRedisTemplate.expire(redisKey,1 , TimeUnit.MINUTES);
        String num = stringRedisTemplate.opsForValue().get(redisKey);


        assert num != null;
        if(Integer.parseInt(num)>=3){
            return Result.fail(ErrorCode.ACCOUNT_FREQUENTLY.getCode(),ErrorCode.ACCOUNT_FREQUENTLY.getMsg());
        }
        assert numCont != null;
        //防止绕过滑块破解密码
        if(Integer.parseInt(numCont)>=100){
            return Result.fail(ErrorCode.ACCOUNT_FREQUENTLY.getCode(),ErrorCode.ACCOUNT_FREQUENTLY.getMsg());
        }
        return null;
    }

    /**
     * 获取token对象
     *
     * @param token
     * @return
     */
    @Override
    public SysUser checkToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        //解析token
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        if (stringObjectMap == null) {
            return null;
        }
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if (StringUtils.isBlank(userJson)) {
            return null;
        }
        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);
        return sysUser;
    }

    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_" + token);
        return Result.success(null);
    }

    @Override
    public Result register(LoginParam loginParam) {
        /**
         * 1. 判断参数 是否合法
         * 2. 判断账户是否存在，存在 返回账户已经被注册
         * 3. 不存在，注册用户
         * 4. 生成token
         * 5. 存入redis 并返回
         * 6. 注意 加上事务，一旦中间的任何过程出现问题，注册的用户 需要回滚
         */
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        String nickname = loginParam.getNickname();
        if (StringUtils.isBlank(account)
                || StringUtils.isBlank(password)
                || StringUtils.isBlank(nickname)
        ) {
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        SysUser sysUser = sysUserService.findUserByAccount(account);
        if (sysUser != null) {
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(), "该账户已经被注册");
        }
        sysUser = new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        sysUser.setPassword(DigestUtils.md5Hex(password + slat));
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("/static/user/user_1.png");
        //1 为true
        sysUser.setAdmin(0);
        // 0 为false
        sysUser.setDeleted(0);
        sysUser.setSalt("");
        sysUser.setStatus("1");
        sysUser.setEmail("");
        this.sysUserService.save(sysUser);

        String token = JWTUtils.createToken(sysUser.getId());

        redisTemplate.opsForValue().set("TOKEN_" + token, JSON.toJSONString(sysUser), 1, TimeUnit.DAYS);
        return Result.success(token);
    }

}
