package com.moneymanager.global.fillter;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception<br>
 * 파일이름       : TraceIdFilter<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 26<br>
 * 설명              : 로그에 사용하는 TraceId를 관리하는 클래스
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
@Component
public class TraceIdFilter extends OncePerRequestFilter {

	private static final DateTimeFormatter ERROR_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String traceId = createTraceId();

		try {
			MDC.put("traceId", traceId);

			filterChain.doFilter(request, response);
		}finally {
			MDC.clear();
		}
	}

	private static String createTraceId() {
		return LocalDateTime.now().format(ERROR_DATE_FORMAT)
				+ "-"
				+ UUID.randomUUID().toString().substring(0, 8);
	}

}
