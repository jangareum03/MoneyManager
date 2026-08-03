package com.moneymanager.global.operation.logger;

import com.moneymanager.global.exception.code.ErrorCode;
import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.operation.enums.OperationResult;
import com.moneymanager.global.operation.OperationContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import static com.moneymanager.global.log.LogFormatterSupport.append;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception<br>
 * 파일이름       : OperationLogger<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 26<br>
 * 설명              : 운영 로그 형식을 지정하는 클래스
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
 * 		 	  <td>26. 6. 26</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Slf4j
@Component
public final class OperationLogger {

	public void success(OperationContext context) {
		String message = build(OperationResult.SUCCESS, context, null);

		log.info(
				"[{}] {}",
				MDC.get("traceId"),
				message
		);
	}

	public void fail(OperationContext context, ApplicationException e) {
		String message = build(OperationResult.FAIL, context, e.getErrorCode());

		log.error(
				"[{}] {}",
				MDC.get("traceId"),
				message
		);
	}

	private String build(OperationResult result,OperationContext context, ErrorCode errorCode) {
		StringBuilder sb = new StringBuilder();

		String action = context.getAction().getTitle();

		sb.append(action).append(" ").append(result.getKorean())
				.append("result=").append(result);

		append(sb, context.getOptions());

		if(result.equals(OperationResult.FAIL)) {
			append(sb, "errorCode", errorCode);
		}

		return sb.toString();
	}

}