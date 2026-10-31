package com.moneymanager.global.logging.aspect;

import com.moneymanager.global.annotation.WorkStep;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.logging<br>
 * 파일이름       : WorkStepAspect<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-10-31<br>
 * 역할             : 개별 기능의 단계의 성공·실패 기록
 * </p>
 */
@Slf4j
@Aspect
@Component
public class WorkStepAspect {

    @Around("@annotation(workStep)")
    public Object around(ProceedingJoinPoint joinPoint, WorkStep workStep) throws Throwable {
        String step = workStep.value();

        try{
            Object result = joinPoint.proceed();

            log.info("step: {}, result: success", step);

            return result;
        }catch (Throwable e){
            log.info("step: {}, result: failure, cause: {}", step,  e.getMessage());

            throw e;
        }
    }

}