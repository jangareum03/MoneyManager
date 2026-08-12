package com.moneymanager.global.operation.aspect;

import com.moneymanager.global.log.AuditLogger;
import com.moneymanager.global.operation.OperationContext;
import com.moneymanager.global.operation.annotation.Operation;
import com.moneymanager.global.operation.enums.OperationResult;
import com.moneymanager.global.operation.holder.OperationContextHolder;
import com.moneymanager.global.security.CustomUserDetails;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.operation.aspect<br>
 * 파일이름       : OperationAspect<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 3<br>
 * 설명              : OperationContext를 설정하는 Aspect 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 * 		<thead>
 * 		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 * 		 	  	<td>날짜</td>
 * 		 	  	<td>작성자</td>
 * 		 	  	<td>변경내용</td>
 * 		 	</tr>
 * 		</thead>
 * 		<tbody>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>26. 8. 3.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Aspect
@Component
public class OperationAspect {

	@Around("@annotation(operation)")
	public Object around(ProceedingJoinPoint joinPoint, Operation operation) throws Throwable {
		//1. 인증된 사용자 조회
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String memberId =  userDetails.getId();

		//2. 클래스와 메서드 조회
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		String className = signature.getDeclaringTypeName();
		String methodName = signature.getMethod().getName();

		//3. 운영정보를 담은 객체 생성
		OperationContext context = OperationContext.builder()
				.action(operation.value())
				.result(OperationResult.SUCCESS)
				.member(memberId)
				.className(className)
				.methodName(methodName)
				.build();

		try {
			OperationContextHolder.set(context);

			return joinPoint.proceed();
		} catch (Throwable e) {
			context.setResult(OperationResult.FAIL);

			throw e;
		} finally {
			AuditLogger.info(context);
		}
	}

}