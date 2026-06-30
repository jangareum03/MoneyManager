package com.moneymanager.exception;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception<br>
 * 파일이름       : ActionContext<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 6. 30<br>
 * 설명              : ServiceAction을 담는 클래스
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
 * 		 	  <td>26. 6. 30</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public final class ActionContext {

	private static final ThreadLocal<ServiceAction> ACTION = new ThreadLocal<>();

	public static void set(ServiceAction action) {
		ACTION.set(action);
	}

	public static ServiceAction get() {
		return ACTION.get();
	}

	public static void clear() {
		ACTION.remove();
	}

}
