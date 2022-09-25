package org.proteam24.zeroneapplication.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("org.proteam24.zeroneapplication.aop.CommonAspect.isServiceLayer() && execution(public * *.*(..))")
    public void anyServiceMethod() {
    }

    @Around("anyServiceMethod() && target(obj) && args(params)")
    public Object addLogging(ProceedingJoinPoint joinPoint, Object obj, Object params) throws Throwable {
        log.info("before - class {}, with params: {}", obj, params);
        try {
            Object result = joinPoint.proceed();
            log.info("after returning - class {}, result {}", obj, result);
            return result;
        } catch (Throwable ex) {
            log.info("after throwing - class {}, exception {}: {}", obj, ex.getClass(), ex.getMessage());
            throw ex;
        } finally {
            log.info("after (finally) - class {}", obj);
        }
    }
}
