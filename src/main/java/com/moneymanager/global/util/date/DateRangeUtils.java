package com.moneymanager.global.util.date;

import com.moneymanager.global.exception.exception.InternalException;
import com.moneymanager.global.log.LogContent;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.moneymanager.global.exception.code.CommonErrorCode.OUT_OF_RANGE;

/**
 * <p>
 * 패키지이름    : com.moneymanager.utils<br>
 * 파일이름       : DateRangeUtils<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 5<br>
 * 설명              : 공통적으로 사용하는 날짜 범위 관련 기능 클래스
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
 * 		 	  <td>26. 1. 5</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>26. 1. 9</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>
 * 		 	      [메서드 이름]
 * 		 	      getListByYearRange → getYearsInRange, getListByMonthRange → getMonthsInRange, getListByDayRange → getDaysInRange
 * 		 	  </td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class DateRangeUtils {

	public static List<Integer> getYearsInRange(int start, int end) {
		if (start <= 0) {
			throw InternalException.of(
					OUT_OF_RANGE,
					LogContent.of(
									"연도 리스트 조회",
									"start",
									start
							).withCause("시작 연도 0이하")
							.withOption("min", 1)
			);
		}

		if (end <= 1) {
			throw InternalException.of(
					OUT_OF_RANGE,
					LogContent.of(
									"연도 리스트 조회",
									"end",
									end
							).withCause("종료 연도 0이하")
							  .withOption("min", 1)
			);
		}

		Year startYear = Year.of(start);
		Year endYear = Year.of(end);

		if (startYear.isAfter(endYear)) {
			throw InternalException.of(
					OUT_OF_RANGE,
					LogContent.ofValues(
							"연도 리스트 조회",
							"start",
							String.valueOf(start),
							"end",
							String.valueOf(end)
					).withCause("시작연도 > 종료연도")
			);
		}

		return createListByRange(start, end);
	}

	public static List<Integer> getMonthsInRange(int start, int end) {
		if (start < 1 || start > 12) {
			throw InternalException.of(
					OUT_OF_RANGE,
					LogContent.of(
							"월 리스트 조회",
							"start",
							start
					).withCause("시작월 범위 초과")
							.withOption("min", 1)
							.withOption("max", 12)
			);
		}

		if (end < 1 || end > 12) {
			throw InternalException.of(
					OUT_OF_RANGE,
					LogContent.of(
							"월 리스트 조회",
							"end",
							end
					).withCause("종료월 범위 초과")
							.withOption("min", 1)
							.withOption("max", 12)
			);
		}

		if( start > end ) {
			throw InternalException.of(
					OUT_OF_RANGE,
					LogContent.ofValues(
							"월 리스트 조회",
							"start",
							String.valueOf(start),
							"end",
							String.valueOf(end)
					).withCause("시작월 > 종료월")
			);
		}

			return createListByRange(start, end);
	}

	public static List<Integer> getDaysInRange(int start, int end) {
		if( start <= 0 || start > 31 ) {
			throw InternalException.of(
					OUT_OF_RANGE,
					LogContent.of(
									"일 리스트 조회",
									"start",
									start
							).withCause("시작일 범위 초과")
							.withOption("min", 1)
							.withOption("max", 31)
			);
		}

		if( end <= 0 || end > 31 ) {
			throw InternalException.of(
					OUT_OF_RANGE,
					LogContent.of(
									"일 리스트 조회",
									"end",
									end
							).withCause("종료일 범위 초과")
							.withOption("min", 1)
							.withOption("max", 31)
			);
		}

		if( start > end ) {
			throw InternalException.of(
					OUT_OF_RANGE,
					LogContent.ofValues(
							"일 리스트 조회",
							"start",
							String.valueOf(start),
							"end",
							String.valueOf(end)
					).withCause("시작일 > 종료일")
			);
		}

		return createListByRange(start, end);
	}


	//===== 유틸 메서드 =====
	private static List<Integer> createListByRange(int start, int end) {
		return IntStream.rangeClosed(start, end)
				.boxed().collect(Collectors.toList());
	}

}
