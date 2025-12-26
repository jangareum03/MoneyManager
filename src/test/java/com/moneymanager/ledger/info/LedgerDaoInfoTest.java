package com.moneymanager.ledger.info;

import com.moneymanager.dao.main.LedgerDao;
import com.moneymanager.domain.ledger.dto.LedgerCategoryDto;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.PaymentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <p>
 * 패키지이름    : com.moneymanager.budgetBook.info<br>
 * 파일이름       : LedgerDaoInfoTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 24.<br>
 * 설명              : 가계부 정보를 DB에서 조회 가능한지 확인하는 테스트 클래스
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
 * 		 	  <td>25. 11. 24.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LedgerDaoInfoTest {

	@Autowired
	private LedgerDao dao;


	//=================================================
	// findLedgerDetailForUser() 테스트
	//=================================================
	@DisplayName("모든 정보가 입력된 가계부 내역은 조회가 되어야 한다.")
	@Test
	void 필수값_반드시_조회가능(){
		//given
		String id = "01H5HZ8X9E7EY2XKZCW2FQX16B";

		Category category = Category.builder().code("010101").name("월급").build();
		Ledger ledger = Ledger.builder()
				.memberId("UCh11001")
				.id(2L)
				.date("20251130")
				.memo("내용 없음")
				.amount(2500000L)
				.paymentType(PaymentType.CASH)
				.placeName("동물병원")
				.roadAddress("서울시 송파구 잠실동 456-78")
				.detailAddress("201동 13층")
				.build();

		LedgerCategoryDto expected = new LedgerCategoryDto(ledger, category);

		//when
		LedgerCategoryDto result = dao.findLedgerDetailForUser(id);

		//then
		assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(expected);
	}

	@DisplayName("필수값 아닌 선택값이 없는 가계부 내역은 조회가 되어야 한다.")
	@Test
	void 필수값_제외한_나머지값_없어도_조회가능(){
		//given
		String id = "01HJF8V8W3KDRJDW86XQRZPD96";

		Category category = Category.builder().code("020902").name("적금").build();
		Ledger ledger = Ledger.builder()
				.date("20251001")
				.amount(20000000L)
				.paymentType(PaymentType.BANK)
				.build();

		LedgerCategoryDto expected = new LedgerCategoryDto(ledger, category);

		//when
		LedgerCategoryDto result = dao.findLedgerDetailForUser(id);

		//then
		assertThat(result.getLedger().getMemo()).isNull();
		assertThat(result.getLedger().getPlaceName()).isNull();
	}

	@DisplayName("존재하지 않는 가계부 내역을 조회하면 DataAccessException 예외가 발생한다.")
	@Test
	void 없는_가계부내역_조회하면_예외발생(){
		//given
		String id ="none1232349ADMVI";

		//when & then
		assertThatExceptionOfType(DataAccessException.class)
				.isThrownBy(() -> dao.findLedgerDetailForUser(id));
	}

}