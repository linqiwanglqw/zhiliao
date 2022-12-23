package com.lin.common.aop;

import com.alibaba.fastjson.JSON;
import com.lin.utils.HttpContextUtils;
import com.lin.utils.IpUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Aspect //切面 定义了通知和切点的关系
@Slf4j
public class LogAspect {

    @Pointcut("@annotation(com.lin.common.aop.LogAnnotation)")
    public void pt() {
    }

    private ThreadPoolExecutor logThreadPoolExecutor =new ThreadPoolExecutor(1,
            1,1,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(100));

    //环绕通知
    @Around("pt()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {

        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = joinPoint.proceed();
        //执行时长(毫秒)
        long endTime=System.currentTimeMillis();
        long time = endTime - beginTime;
        log.info("=====================log start==============================");
        //获取request 设置IP地址
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        log.info("ip:{}", IpUtils.getIpAddr(request));

        logThreadPoolExecutor.execute(()->{
            //用异步保存日志
            recordLog(joinPoint, time);
        });
        return result;
    }

    private void recordLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);

        log.info("module:{}", logAnnotation.module());
        log.info("operation:{}", logAnnotation.operator());

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.info("request method:{}", className + "." + methodName + "()");

        //请求的参数
        Object[] args = joinPoint.getArgs();
        if(args.length!=0){
            for (int i = 0; i < args.length; i++) {
                String params = JSON.toJSONString(args[i]);
                log.info("params {}:{}",i+1, params);
            }
        }

        log.info("excute time : {} ms", time);
        log.info("=====================log end================================");
    }
}
