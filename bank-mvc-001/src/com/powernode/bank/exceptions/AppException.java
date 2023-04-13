package com.powernode.bank.exceptions;

/**
 * App异常
 * @author 老杜
 * @version 1.0
 * @since 1.0
 */
public class AppException extends Exception{
    public AppException(){}
    public AppException(String msg){
        super(msg);
    }
}
