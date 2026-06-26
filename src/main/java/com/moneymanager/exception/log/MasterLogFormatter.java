package com.moneymanager.exception.log;

import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.ServiceAction;

import java.util.Map;

import static com.moneymanager.exception.log.LogFormatterSupport.append;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception<br>
 * 파일이름       : MasterLogFormatter<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 26<br>
 * 설명              : 최종 로그 형식을 지정하는 클래스
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
public final class MasterLogFormatter {

	public static String success(ServiceAction action, Map<String, Object> context) {
		return build(action.getTitle(), true, context, null);
	}

	public static String fail(BusinessException e) {
		return build(
				e.getServiceAction().getTitle(),
				false,
				e.getLogInfo().getContext(),
				e.getErrorCode()
		);
	}

	private static String build(String title, boolean success, Map<String, Object> context, ErrorCode errorCode) {
		StringBuilder sb = new StringBuilder();

		sb.append(title)
				.append(success ? " 성공" : " 실패")
				.append("result=")
				.append(success ? "success" : "fail");

		append(sb, context);

		if(!success) {
			append(sb, "errorCode", errorCode);
		}

		return sb.toString();
	}

}
