package com.areum.moneymanager.exception.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.exception.handler<br>
 *  * 파일이름       : WebExceptionHandler<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 23<br>
 *  * 설명              : Web 요청에서 예외가 발생하면 처리하는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 7. 23</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>최초 생성(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@ControllerAdvice(basePackages = "com.areum.moneymanager.controller.web")
public class WebExceptionHandler {


}
