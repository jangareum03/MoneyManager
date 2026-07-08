package com.moneymanager.exception.log;

import com.moneymanager.exception.exception.ApplicationException;

import static com.moneymanager.exception.log.LogFormatterSupport.append;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception<br>
 * 파일이름       : DevelopmentLogFormatter<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 26<br>
 * 설명              : 상세로그 형식을 지정하는 클래스
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
public final class DevelopmentLogFormatter {

	private DevelopmentLogFormatter() {}

	public static String detail(ApplicationException e) {
		DeveloperLogInfo log = e.getDeveloperLog();

		StringBuilder sb = new StringBuilder();

		sb.append(log.getWork()).append(" 실패");

		append(sb, "cause", log.getCause());
		append(sb, "class", log.getSourceClass());
		append(sb, "method", log.getSourceMethod());
		append(sb, "target", log.getTarget() == null ? null : log.getTarget().getSimpleName());
		append(sb, "field", log.getField());
		append(sb, log.getOptions());
		append(sb, "value", log.getValue());

		return sb.toString();
	}

}