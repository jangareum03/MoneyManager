package com.moneymanager.support.security;

import com.moneymanager.support.data.MemberTestData;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

	String memberId() default MemberTestData.MEMBER_ID;

	String username() default MemberTestData.USERNAME;

	String password() default MemberTestData.PASSWORD;

	String role() default "USER";

}
