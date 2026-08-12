package com.moneymanager.global.operation.aspect;

import com.moneymanager.global.operation.OperationContext;
import com.moneymanager.global.operation.holder.OperationContextHolder;
import com.moneymanager.global.operation.logger.OperationLogger;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.operation.aspect<br>
 * 파일이름       : LoggingAspect<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 3<br>
 * 설명              : 로그를 작성하는 Aspect 클래스
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
 * 		 	  <td>26. 8. 3</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

	private final OperationLogger logFormatter;

	@AfterReturning(
			pointcut = "execution(* com.moneymanager..service..*Service(..))",
			returning = "result"
	)
	public void success(Object result) {
		OperationContext context = OperationContextHolder.get();

		if(context == null) {
			return;
		}

		logFormatter.success(context);
	}

}