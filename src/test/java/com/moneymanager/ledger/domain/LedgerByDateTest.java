package com.moneymanager.ledger.domain;

import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.ledger.vo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain<br>
 * 파일이름       : LedgerByDateTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 1<br>
 * 설명              : LedgerByDate 클래스를 검증하는 테스트 클래스
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
 * 		 	  <td>25. 12. 1.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class LedgerByDateTest {

	@DisplayName("Ledger 객체가 null이면 리스트에 추가되지 않는다.")
	@Test
	void LedgerSummary_null이면_추가실패(){
		//when
		LedgerByDate result = new LedgerByDate(Collections.emptyList());

		//then
		assertThat(result.getDateGroups()).isNotNull();
		assertThat(result.getDateGroups().size()).isZero();
	}

	@Test
	@DisplayName("중복되지 않은 Ledger 객체는 리스트에 추가된다.")
	void LedgerSummary_없으면_추가완료(){
		//given
		List<Ledger>  obj = List.of(
				Ledger.builder()
						.id("01AN4Z07BY79KA1307SR9X4MV3")
						.date(new LedgerDate("20251101"))
						.category(Category.builder().name("간식").code("020101").build())
						.memo("팥빵 2개, 우유 1개")
						.amountInfo(AmountInfo.builder().amount(8800L).type(PaymentType.CARD).build())
						.build(),
				Ledger.builder()
						.id("01H5HZ8X9E7EY2XKZCW2FQX16B")
						.date(new LedgerDate("20251102"))
						.category(Category.builder().name("택시").code("020302").build())
						.amountInfo(AmountInfo.builder().amount(12500L).type(PaymentType.CASH).build())
						.build()
		);

		//when
		LedgerByDate result = new LedgerByDate(obj);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getDateGroups()).hasSize(2);
		assertThat(result.getDateGroups().get("2025. 11. 01 (토)").size()).isEqualTo(1);
	}

	@DisplayName("중복인 Ledger 객체는 리스트에 추가되지 않는다.")
	@Test
	void LedgerSummary_이미있으면_추가실패(){
		//given
		List<Ledger>  obj = List.of(
				Ledger.builder()
						.id("01AN4Z07BY79KA1307SR9X4MV3")
						.date(new LedgerDate("20251101"))
						.category(Category.builder().name("간식").code("020101").build())
						.memo("팥빵 2개, 우유 1개")
						.amountInfo(AmountInfo.builder().amount(8800L).type(PaymentType.CARD).build())
						.build(),
				Ledger.builder()
						.id("01H5HZ8X9E7EY2XKZCW2FQX16B")
						.date(new LedgerDate("20251105"))
						.category(Category.builder().name("택시").code("020302").build())
						.amountInfo(AmountInfo.builder().amount(12500L).type(PaymentType.CASH).build())
						.build(),
				Ledger.builder()
						.id("01AN4Z07BY79KA1307SR9X4MV3")
						.date(new LedgerDate("20251101"))
						.category(Category.builder().name("간식").code("020101").build())
						.memo("팥빵 2개, 우유 1개")
						.amountInfo(AmountInfo.builder().amount(8800L).type(PaymentType.CARD).build())
						.build(),
				Ledger.builder()
						.id("01ARZ3NDEKTSV4RRFFQ69G5FAF")
						.date(new LedgerDate("20251120"))
						.category(Category.builder().name("용돈").code("010101").build())
						.memo("11월 용돈")
						.amountInfo(AmountInfo.builder().amount(200000L).type(PaymentType.BANK).build())
						.build()
		);

		//when
		LedgerByDate result = new LedgerByDate(obj);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getDateGroups()).hasSize(3);
		assertThat(result.getDateGroups().get("2025. 11. 01 (토)").size()).isEqualTo(1);
		assertThat(result.getDateGroups().get("2025. 11. 05 (수)").size()).isEqualTo(1);
		assertThat(result.getDateGroups().get("2025. 11. 20 (목)").size()).isEqualTo(1);
	}

	@DisplayName("가계부 날짜가 동일하면 Ledger 객체는 기존 리스트에 추가된다.")
	@Test
	void 키_이미있으면_기존리스트_추가완료(){
		//given
		List<Ledger>  obj = List.of(
				Ledger.builder()
						.id("01AN4Z07BY79KA1307SR9X4MV3")
						.date(new LedgerDate("20251101"))
						.category(Category.builder().name("간식").code("020101").build())
						.memo("팥빵 2개, 우유 1개")
						.amountInfo(AmountInfo.builder().amount(8800L).type(PaymentType.CARD).build())
						.build(),
				Ledger.builder()
						.id("01H5HZ8X9E7EY2XKZCW2FQX16B")
						.date(new LedgerDate("20251101"))
						.category(Category.builder().name("택시").code("020302").build())
						.amountInfo(AmountInfo.builder().amount(12500L).type(PaymentType.CASH).build())
						.build(),
				Ledger.builder()
						.id("01ARZ3NDEKTSV4RRFFQ69G5FAV")
						.date(new LedgerDate("20251105"))
						.category(Category.builder().name("간식").code("020101").build())
						.memo("떡볶이")
						.amountInfo(AmountInfo.builder().amount(5000L).type(PaymentType.CARD).build())
						.build()
		);

		//when
		LedgerByDate result = new LedgerByDate(obj);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getDateGroups()).hasSize(2);
		assertThat(result.getDateGroups().get("2025. 11. 01 (토)").size()).isEqualTo(2);
		assertThat(result.getDateGroups().get("2025. 11. 05 (수)").size()).isEqualTo(1);
	}

	@DisplayName("가계부 날짜가 동일하지 않으면 LedgerSummary 객체는 새로운 리스트에 추가된다.")
	@Test
	void 키_없으면_새로운리스트_추가완료(){
		//given
		List<Ledger>  obj = List.of(
				Ledger.builder()
						.id("01AN4Z07BY79KA1307SR9X4MV3")
						.date(new LedgerDate("20251101"))
						.category(Category.builder().name("간식").code("020101").build())
						.memo("팥빵 2개, 우유 1개")
						.amountInfo(AmountInfo.builder().amount(8800L).type(PaymentType.CARD).build())
						.build(),
				Ledger.builder()
						.id("01ARZ3NDEKTSV4RRFFQ69G5FAV")
						.date(new LedgerDate("20251105"))
						.category(Category.builder().name("간식").code("020101").build())
						.memo("떡볶이")
						.amountInfo(AmountInfo.builder().amount(5000L).type(PaymentType.CARD).build())
						.build()
		);

		//when
		LedgerByDate result = new LedgerByDate(obj);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getDateGroups()).hasSize(2);
		assertThat(result.getDateGroups().get("2025. 11. 01 (토)").size()).isEqualTo(1);
		assertThat(result.getDateGroups().get("2025. 11. 05 (수)").size()).isEqualTo(1);
	}
}
