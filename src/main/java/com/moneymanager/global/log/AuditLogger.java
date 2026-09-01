package com.moneymanager.global.log;

import com.moneymanager.global.operation.OperationContext;
import com.moneymanager.global.operation.enums.OperationResult;
import com.moneymanager.global.operation.enums.ServiceAction;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.slf4j.helpers.MessageFormatter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.log<br>
 * 파일이름       : AuditLogger<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 12<br>
 * 설명              : 운영 로그 출력 및 기록 역할하는 클래스
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
public class AuditLogger {

	public static void info(OperationContext context) {
		String trace = MDC.get("traceId");
		ServiceAction action = context.getAction();
		String result = String.valueOf(context.getResult());

		StringBuilder message = new StringBuilder();

		message.append(String.format("[%-36s]", trace));

		LogFormatterSupport.append(message, "action", action.name());
		LogFormatterSupport.append(message, "result", result);
		LogFormatterSupport.append(message, "member", context.getMember());

		if(context.getResult() == OperationResult.FAIL) {
			LogFormatterSupport.append(message, "errorCode", context.getOptions().get("error"));
			LogFormatterSupport.append(message, "message", action.getDescription() + " 실패");
		}else {
			LogFormatterSupport.append(message, "message", action.getDescription() + " 성공");
		}

		log.info(message.toString());
	}

	public static void warn(String message, String... values) {
		String trace = MDC.get("traceId");

		String formattedMessage = MessageFormatter.arrayFormat(message, values).getMessage();

		log.warn("[{}] {}", trace, formattedMessage);
	}

}