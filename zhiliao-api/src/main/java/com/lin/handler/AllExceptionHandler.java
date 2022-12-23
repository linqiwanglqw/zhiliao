package com.lin.handler;

import com.lin.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//对加了@Controller注解的方法进行拦截处理 AOP的实现
@ControllerAdvice
@Slf4j
public class AllExceptionHandler {
    //进行异常处理，处理Exception.class的异常
    @ExceptionHandler(Exception.class)
    @ResponseBody //返回json数据
    public Result doException(Exception ex) {
        //打印异常堆栈信息
        ex.printStackTrace();
        log.error(String.valueOf(ex));
        return Result.fail(520, "系统维护中");
    }

    //进行异常处理，处理ServiceException.class的异常
    @ExceptionHandler(ServiceException.class)
    @ResponseBody //返回json数据
    public Result doServiceException(ServiceException ex) {
//        ex.printStackTrace();
        log.error(String.valueOf(ex));
        return Result.fail(521, "请勿频繁操作");
    }

}
