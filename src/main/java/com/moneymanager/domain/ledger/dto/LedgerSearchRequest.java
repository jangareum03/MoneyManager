package com.moneymanager.domain.ledger.dto;

import com.moneymanager.domain.global.dto.DateRequest;
import com.moneymanager.domain.ledger.enums.DateType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.budgetBook.request<br>
 * * 파일이름       : LedgerSearchRequest<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 29.<br>
 * * 설명              : 가계부 검색 요청을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 29.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LedgerSearchRequest {
	//타입
	private String type;
	//년
	private Integer year;
	//월
	private Integer month;
	//주
	private Integer week;
	//검색 유형
	private String mode;
	//검색 키워드
	private List<String> keywords;


	/**
	 * 기본값을 설정한 {@link LedgerSearchRequest} 객체를 반환합니다.
	 * <p>
	 *     기본 설정:
	 *     <ul>
	 *         <li>조회 단위: 월 기준({@link DateType#MONTH})</li>
	 *         <li>연도, 월: 오늘 날짜 기준</li>
	 *         <li>주차: null</li>
	 *         <li>검색모드: "all"</li>
	 *         <li>검색 키워드: null</li>
	 *     </ul>
	 * </p>
	 *
	 * @return	기본값이 설정된 {@link LedgerSearchRequest} 객체
	 */
	public static LedgerSearchRequest getDefaultValue() {
		LocalDate today = LocalDate.now();

		return LedgerSearchRequest.builder()
				.type(DateType.MONTH.getType())
				.year(today.getYear())
				.month(today.getMonthValue())
				.week(null)
				.mode("all")
				.keywords(null)
				.build();
	}
}
