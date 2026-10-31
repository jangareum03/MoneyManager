package com.moneymanager.global.annotation;

import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.annotation<br>
 * 파일이름       : ViewController<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-11-01<br>
 * 역할             : View 컨트롤러 알려주는 어노테이션
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
public @interface ViewController {
}