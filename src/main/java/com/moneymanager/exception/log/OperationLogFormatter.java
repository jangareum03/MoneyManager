package com.moneymanager.exception.log;

import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.exception.ApplicationException;

import static com.moneymanager.exception.log.LogFormatterSupport.append;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception<br>
 * 파일이름       : OperationLogFormatter<br>
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
public final class OperationLogFormatter {

	public static String success(OperationContext context) {
		return build(OperationResult.SUCCESS, context, null);
	}

	public static String fail(OperationContext context, ApplicationException e) {
		return build(OperationResult.FAIL, context, e.getErrorCode());
	}

	private static String build(OperationResult result,OperationContext context, ErrorCode errorCode) {
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
