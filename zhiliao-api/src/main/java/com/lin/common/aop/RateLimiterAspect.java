package com.lin.common.aop;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import cn.hutool.core.util.ArrayUtil;
import com.lin.handler.ServiceException;
import com.lin.utils.HttpContextUtils;
import com.lin.utils.IpUtils;
import com.lin.utils.UserThreadLocal;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 接口限流
 */
@Aspect
@Component
public class RateLimiterAspect {
    private static final Logger log = LoggerFactory.getLogger(RateLimiterAspect.class);

    private RedisTemplate<Object, Object> redisTemplate;

    private RedisScript<Long> limitScript;

    @Autowired
    public void setRedisTemplate1(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Autowired
    public void setLimitScript(RedisScript<Long> limitScript) {
        this.limitScript = limitScript;
    }

    @Before("@annotation(rateLimiter)")
    public void doBefore(JoinPoint point, RateLimiter rateLimiter) {
        //获得注解中的配置信息
        int time = rateLimiter.time();
        int count = rateLimiter.count();
        String combineKey = getCombineKey(rateLimiter, point);
        //Collections.singletonList()返回的是不可变的集合，但是这个长度的集合只有1，可以减少内存空间。
        List<Object> keys = Collections.singletonList(combineKey);
        try
        {
            Long number = redisTemplate.execute(limitScript, keys, count, time);
            if (ArrayUtil.isEmpty(number) || number.intValue() > count)
            {
                throw new ServiceException("访问过于频繁，请稍候再试");
            }
            log.info("限制请求'{}',当前请求'{}',缓存key'{}'", count, number.intValue(), combineKey);
        }
        catch (Exception e)
        {
            throw new ServiceException("访问过于频繁，请稍候再试");
        }
    }

    public String getCombineKey(RateLimiter rateLimiter, JoinPoint point){
        StringBuffer stringBuffer = new StringBuffer(rateLimiter.key());
        if (rateLimiter.limitType() == LimitType.IP) {
            //获取request 设置IP地址
            HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
            log.info("ip:{}", IpUtils.getIpAddr(request));
            stringBuffer.append(IpUtils.getIpAddr(request)).append("::");
        }
        // 获取方法名和参数列表
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = method.getDeclaringClass();
        stringBuffer.append(targetClass.getName()).append("::").append(method.getName());

        //获取当前用户id
        Long sysUserId = UserThreadLocal.get().getId();
        if(ArrayUtil.isNotEmpty(sysUserId)){
            //获得 rate_limit:0:0:0:0:0:0:0:1-com.lin.controller.ArticleController-test-userid
            return stringBuffer.append("::").append(sysUserId).toString();
        }

        //获得 rate_limit:0:0:0:0:0:0:0:1-com.lin.controller.ArticleController-test
        return stringBuffer.toString();
    }
}
