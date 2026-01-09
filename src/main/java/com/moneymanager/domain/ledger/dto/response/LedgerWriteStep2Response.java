package com.moneymanager.domain.ledger.dto.response;

import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.enums.LedgerType;
import com.moneymanager.domain.ledger.enums.PaymentType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto<br>
 * 파일이름       : LedgerWriteStep2Response<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 19<br>
 * 설명              : 가계부 상세 작성 응답을 위한 데이터 클래스
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
 * 		 	  <td>25. 12. 19.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class LedgerWriteStep2Response {
	private final String title;												//제목
	private final LedgerType type;										//가계부 유형
	private final List<FixedYN> fixed;								//고정여부
	private final List<CategoryResponse> categories;		//카테고리 리스트
	private final List<PaymentType> paymentTypes;		//결제유형
	private final List<Boolean> imageSlot;							//이미지 사용여부

	private LedgerWriteStep2Response(String title, LedgerType type, List<CategoryResponse> categories, List<Boolean> imageSlot) {
		this.title = title;
		this.categories = categories;
		this.imageSlot =imageSlot;
		this.type = type;

		this.fixed = List.of(FixedYN.values());
		this.paymentTypes = List.of(PaymentType.values());
	}

	/**
	 * 수입 유형에 필요한 가계부 작성 2단계의 기본 데이터를 생성한다.
	 * <p>
	 *     화면 제목, 중분류 수입 카테고리 목록과 이미지 슬롯 사용 여부 정보를 담은 {@code boolean}형 리스트를 포함한 응답 객체를 반환합니다.
	 * </p>
	 *
	 * @param title				화면에 보일 제목
	 * @param categories	중간 단계({@link com.moneymanager.domain.ledger.enums.CategoryLevel#MIDDLE})의 수입 카테고리 리스트
	 * @param imageSlot		사용 여부를 담은 이미지 슬롯 리스트
	 * @return	가계부 작성 2단계에 필요한 정보를 담은 객체
	 */
	public static LedgerWriteStep2Response ofDataByIncome(String title, List<CategoryResponse> categories, List<Boolean> imageSlot){
		return new LedgerWriteStep2Response(title, LedgerType.INCOME, categories, imageSlot);
	}


	/**
	 * 지출 유형에 필요한 가계부 작성 2단계의 기본 데이터를 생성한다.
	 * <p>
	 *     화면 제목, 중분류 지출 카테고리 목록과 이미지 슬롯 사용 여부 정보를 담은 {@code boolean}형 리스트를 포함한 응답 객체를 반환합니다.
	 * </p>
	 *
	 * @param title				화면에 보일 제목
	 * @param categories	중간 단계({@link com.moneymanager.domain.ledger.enums.CategoryLevel#MIDDLE})의 지출 카테고리 리스트
	 * @param imageSlot		사용 여부를 담은 이미지 슬롯 리스트
	 * @return	가계부 작성 2단계에 필요한 정보를 담은 객체
	 */
	public static LedgerWriteStep2Response ofDataByOutlay(String title, List<CategoryResponse> categories, List<Boolean> imageSlot){
		return new LedgerWriteStep2Response(title, LedgerType.OUTLAY, categories, imageSlot);
	}
}
