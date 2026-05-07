package com.sky.aop;


import com.sky.annotation.Autofill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class AutoFillAspect {
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.Autofill)")
    private void pt(){}

    @Before("pt()")
    public void doBefore(JoinPoint joinPoint) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//获得方法签名对象
        Autofill annotation = signature.getMethod().getAnnotation(Autofill.class);//获得方法上的注解对象
        OperationType type = annotation.value();//获得数据操作类型

        Object[] args = joinPoint.getArgs();

        //如果切入点无参则返回
        if(args == null || args.length == 0) return;

        LocalDateTime now = LocalDateTime.now();
        Long id = BaseContext.getCurrentId();

        //约定Category对象放到首位
        Object object = args[0];

        //通过反射设置参数
        if(type == OperationType.INSERT) {
            Method setCreateTime = object.getClass().getMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setCreateUser = object.getClass().getMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateTime = object.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = object.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            setCreateTime.invoke(object,now);
            setCreateUser.invoke(object,id);
            setUpdateTime.invoke(object,now);
            setUpdateUser.invoke(object,id);
        } else if(type == OperationType.UPDATE) {
            Method setUpdateTime = object.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = object.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            setUpdateTime.invoke(object,now);
            setUpdateUser.invoke(object,id);
        }

    }
}
