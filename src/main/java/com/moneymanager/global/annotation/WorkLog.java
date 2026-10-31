package com.moneymanager.global.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.logging<br>
 * 파일이름       : WorkLog<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-10-31<br>
 * 역할             : 로그를 적용할 작업 지정
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WorkLog {

    String value() default "";

}