package org.proteam24.zeroneapplication.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class CommonAspect {

    @Pointcut("within(org.proteam24.zeroneapplication.service.*Service)")
    public void isServiceLayer() {
    }
}
