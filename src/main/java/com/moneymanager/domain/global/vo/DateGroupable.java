package com.moneymanager.domain.global.vo;

import java.time.LocalDate;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.global.vo<br>
 * 파일이름       : DateGroupable<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 20.<br>
 * 설명              : 공통적으로 날짜값을 나타내는 인터페이스
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
 * 		 	  <td>25. 11. 20.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public interface DateGroupable {

	/**
	 *	조회단위에 해당하는 기간의 시작날짜를 반환합니다.
	 *<p>
	 *	구현체에서 해당 메서드는 기간의 시작일을 계산하여 {@link LocalDate}로 반환해야 합니다.
	 *
	 * @return	해당 기간의 시작 날짜
	 */
	LocalDate getStartDate();


	/**
	 * 조회단위에 해당하는 기간의 종료날짜를 반환합니다.
	 * <p>
	 * 구현체에서 해당 메서드는 기간의 종료일을 계산하여 {@link LocalDate}로 반환해야 합니다.
	 *
	 * @return	해당 기간의 종료 날짜
	 */
	LocalDate getEndDate();
}
