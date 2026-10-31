package com.moneymanager.global.logging.aspect;

import com.moneymanager.global.annotation.WorkLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.logging<br>
 * 파일이름       : WorkLogAspect<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-10-31<br>
 * 역할             : 작업 시작 및 최종 성공·실패 기록
 * </p>
 */
@Slf4j
@Aspect
@Component
public class WorkLogAspect {

    @Around("@annotation(workLog)")
    public Object logging(ProceedingJoinPoint joinPoint, WorkLog workLog) throws Throwable {
        String work = workLog.value();
        Object[] args = joinPoint.getArgs();

        long startTime = System.currentTimeMillis();

        log.info("{} 시작", work);

        try{
            Object result = joinPoint.proceed();

            long endTime = System.currentTimeMillis();

            log.info("{} 성공, time: {}", work,  endTime - startTime);

            return result;
        } catch (Throwable e){
            long endTime = System.currentTimeMillis();

            log.info("{} 실패, time: {}", work,  endTime - startTime);

            throw e;
        }
    }

}