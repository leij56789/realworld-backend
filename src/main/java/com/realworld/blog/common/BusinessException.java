package com.realworld.blog.common;
/**
 * @author jiaolei
 * @date 2026/6/4 18:03
 * @description 
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message){
        super(message);
    }
}
