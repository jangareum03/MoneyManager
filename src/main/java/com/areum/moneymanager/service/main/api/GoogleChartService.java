package com.areum.moneymanager.service.main.api;

import com.areum.moneymanager.dto.common.request.DateRequest;
import com.areum.moneymanager.dto.external.google.GoogleChartResponse;
import com.areum.moneymanager.enums.type.DateType;
import com.areum.moneymanager.service.main.BudgetBookService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.service.main.api<br>
 *  * 파일이름       : GoogleChartService<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 22. 11. 16<br>
 *  * 설명              : 구글 차트 관련 비즈니스 로직을 처리하는 클래스
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
 *		 	  <td>22. 11. 26</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>최초 생성(버전 1.0)</td>
 *		 	</tr>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Service
public class GoogleChartService {

	private final Logger logger = LogManager.getLogger(this);
	private final BudgetBookService budgetBookService;

	public GoogleChartService( BudgetBookService budgetBookService ) {
		this.budgetBookService = budgetBookService;
	}



	/**
	 * 가계부 차트에 필요한 자료를 만든 후 반환하는 메서드
	 *
	 * @param memberId			회원 식별번호
	 * @param date			검색할 가계부 날짜 값
	 * @return	차트에 필요한 자료들을 담은 리스트
	 */
	public List<Object> createChartData( String memberId, DateRequest date ) {
		DateType type = date.getType();

		List<Object> data = new ArrayList<>();
		List<GoogleChartResponse> chartList = budgetBookService.getBudgetBookForChart( memberId, date );

		//헤어와 본문 설정
		switch ( type ) {
			case WEEK:
				// 범위가 주간인 경우
				data.add( List.of( type.getText(), "수입", "지출") );

				for( GoogleChartResponse chart : chartList ) {
					data.add( List.of( chart.getLabel(), chart.getIncomePrice(), chart.getOutlayPrice() ) );
				}
				break;
			case YEAR:
				// 범위가 연간인 경우
				data.add(List.of( type.getText(), "수입", "지출")) ;

				for( GoogleChartResponse chart : chartList ) {
					data.add( List.of( chart.getLabel(), chart.getIncomePrice(), chart.getOutlayPrice() ) );
				}
				break;
			case MONTH:
			default:
				//카테고리 별 색상지정
				String[] colors = { "#FFA500", "#1E90FF", "#FFD700", "#FF69B4", "#32CD32", "#8A2BE2", "#1E3A5F", "#DC143C", "#006400" };
				// 범위가 월간인 경우
				data.add(List.of( "카테고리", "지출", Map.of("role", "style") ));

				for( int i=0; i< chartList.size(); i++ ) {
					GoogleChartResponse chart = chartList.get(i);
					data.add( List.of( chart.getLabel(), chart.getOutlayPrice(), "color: " + colors[i]) );
				}
		}


		return data;
	}




}
