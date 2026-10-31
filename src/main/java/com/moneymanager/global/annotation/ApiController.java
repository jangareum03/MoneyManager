package com.moneymanager.global.annotation;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.exception.annotation<br>
 * 파일이름       : ApiController<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-08-24<br>
 * 역할             : API 컨트롤러 알려주는 어노테이션
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
public @interface ApiController {
}