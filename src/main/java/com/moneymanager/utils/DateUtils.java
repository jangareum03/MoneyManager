package com.moneymanager.utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>
 * 패키지이름    : com.moneymanager.utils<br>
 * 파일이름       : DateUtils<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 5<br>
 * 설명              : 공통적으로 사용하는 날짜 기능 클래스
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
 * 		 	  <td>26. 1. 5.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class DateUtils {

	/**
	 * 지정된 시작연도({@code start})와 종료연도({@code end}) 범위 내에 있는 연도를 정수형으로 담아 리스트로 반환합니다.
	 * <p>
	 * 다음과 같은 경우에는 {@link IllegalArgumentException} 예외가 발생합니다.
	 *     <ul>
	 *         <li>연도가 0이하인 경우</li>
	 *         <li>시작 연도가 종료연도보다 큰 경우</li>
	 *     </ul>
	 * </p>
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	DateUtils.getListByYearRange(2020, 2025);	// [2020, 2021, 2022, 2023, 2024, 2025]
	 *     	DateUtils.getListByYearRange(1900, 1900);	// [1900]
	 *     }</pre>
	 * </p>
	 *
	 * @param start 시작 연도(포함)
	 * @param end   종료 연도(포함)
	 * @return 시작부터 종료까지의 연도를 오름차순으로 담은 정수 리스트
	 */
	public static List<Integer> getListByYearRange(int start, int end) {
		if (start <= 0 || end <= 0) {
			throw new IllegalArgumentException(
					String.format("연도는 0보다 커야합니다. (start: %d, end: %d)", start, end)
			);
		}

		if (start > end) {
			throw new IllegalArgumentException(
					String.format("시작연도가 종료연도 보다 큽니다. (start: %d, end: %d)", start, end)
			);
		}

		return getListByRange(start, end);
	}


	/**
	 * 지정된 시작월({@code start})과 종료월({@code end}) 범위 내에 있는 월을 정수형으로 담아 리스트로 반환합니다.
	 * <p>
	 * 다음과 같은 경우에는 {@link IllegalArgumentException} 예외가 발생합니다.
	 *     <ul>
	 *         <li>월이 범위에서 벗어난 경우</li>
	 *         <li>시작 월이 종료월 보다 큰 경우</li>
	 *     </ul>
	 * </p>
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	DateUtils.getListByMonthRange(1, 3);	// [1, 2, 3]
	 *     	DateUtils.getListByMonthRange(9, 12);	// [9, 10, 11, 12]
	 *     }</pre>
	 * </p>
	 *
	 * @param start 시작 월(포함)
	 * @param end   종료 월(포함)
	 * @return 시작부터 종료까지의 월을 오름차순으로 담은 정수 리스트
	 */
	public static List<Integer> getListByMonthRange(int start, int end) {
		if (start < 1 || start > 12) {
			throw new IllegalArgumentException(
					String.format("월은 1~12 사이여야 합니다. (start: %d)", start)
			);
		}

		if (end < 1 || end > 12) {
			throw new IllegalArgumentException(
					String.format("월은 1~12 사이여야 합니다. (end: %d)", end)
			);
		}

		if( start > end ) {
			throw new IllegalArgumentException(
					String.format("시작월이 종료월 보다 큽니다. (start: %d, end: %d)", start, end)
			);
		}

			return getListByRange(start, end);
	}


	/**
	 * 지정된 시작일({@code start})과 종료일({@code end}) 범위 내에 있는 일을 정수형으로 담아 리스트로 반환합니다.
	 * <p>
	 * 다음과 같은 경우에는 {@link IllegalArgumentException} 예외가 발생합니다.
	 *     <ul>
	 *         <li>일이 범위에서 벗어난 경우</li>
	 *         <li>시작일이 종료일 보다 큰 경우</li>
	 *     </ul>
	 * </p>
	 * <p>
	 *     예제 사용법:
	 *     <pre>{@code
	 *     	DateUtils.getListByDayRange(1, 31);	// [1, 2, 3, 4, ... , 30, 31]
	 *     	DateUtils.getListByDayRange(9, 12);	// [9, 10, 11, 12]
	 *     }</pre>
	 * </p>
	 *
	 * @param start 시작일(포함)
	 * @param end   종료일(포함)
	 * @return 시작부터 종료까지의 일을 오름차순으로 담은 정수 리스트
	 */
	public static List<Integer> getListByDayRange(int start, int end) {
		if( start <= 0 || start > 31 ) {
			throw new IllegalArgumentException(
					String.format("일은 1~31 사이여야 합니다. (start: %d)", start)
			);
		}

		if( end <= 0 || end > 31 ) {
			throw new IllegalArgumentException(
					String.format("일은 1~31 사이여야 합니다. (end: %d)", end)
			);
		}

		if( start > end ) {
			throw new IllegalArgumentException(
					String.format("시작일이 종료일보다 큽니다. (start: %d, end: %d)", start, end)
			);
		}

		return getListByRange(start, end);
	}

	//범위(포함)내의 정수를 리스트로 담아서 반환
	private static List<Integer> getListByRange(int start, int end) {
		return IntStream.rangeClosed(start, end)
				.boxed().collect(Collectors.toList());
	}

}
