package com.realworld.blog.common;

import lombok.NoArgsConstructor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * @author jiaolei
 * @date 2026/6/4 15:57
 * @description 
 */
@RestControllerAdvice
@NoArgsConstructor
public class GlobalExceptionHandler {
    /*
    * 处理业务异常（如：用户不存在）
    * */
    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e){
        return Result.error(e.getMessage());
    }
    /*
    * 处理参数校验异常（@Valid 校验失败）
    * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidationException(MethodArgumentNotValidException e){
        String message=e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return Result.error(400,message);
    }
    /*
    * 处理其他未捕获的异常
    * */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e){
        e.printStackTrace();//打印日志
        return Result.error(500,"服务器内部错误："+e.getMessage());
    }
}
