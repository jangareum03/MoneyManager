package com.moneymanager.global.log;

import com.moneymanager.global.operation.OperationContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.log<br>
 * 파일이름       : DevLogger<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 12<br>
 * 설명              : 개발 로그 출력 및 기록 역할하는 클래스
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
 * 		 	  <td>26. 8. 12</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
public class DevLogger {

	public static void debug(OperationContext context) {
		String trace = MDC.get("traceId");
		String className = context.getClassName();
		String methodName = context.getMethodName();

		LogContent logContent = (LogContent) context.getOptions().get("log");

		StringBuilder message = new StringBuilder();

		message.append("[")
					.append(trace)
					.append("] ");

		LogFormatterSupport.append(message, "work", logContent.getWork());
		LogFormatterSupport.append(message, "cause", logContent.getCause());
		LogFormatterSupport.append(message, "class", className);
		LogFormatterSupport.append(message, "method", methodName);

		if(logContent.getTarget() != null) {
			LogFormatterSupport.append(message, "target", logContent.getTarget().getSimpleName());
		}

		LogFormatterSupport.append(message, "field", logContent.getField());

		if(logContent.getOptions() != null && !logContent.getOptions().isEmpty()) {
			LogFormatterSupport.append(message, logContent.getOptions());
		}

		LogFormatterSupport.append(message, "value", logContent.getValue());

		log.debug(message.toString());
	}

}