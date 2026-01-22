package com.moneymanager.unit.repository.ledger;

import com.moneymanager.config.DatabaseConfig;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.enums.FixedPeriod;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.ledger.vo.Place;
import com.moneymanager.repository.ledger.LedgerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.repository.ledger<br>
 * 파일이름       : LedgerRepositoryTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 10<br>
 * 설명              : 가계부와 관련된 테이블의 데이터 조작 기능을 검증하는 클래스
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
 * 		 	  <td>26. 1. 10.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@JdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
		DatabaseConfig.class,
		LedgerRepository.class
})
public class LedgerRepositoryTest {

	@Autowired	private LedgerRepository repository;

	@Autowired	private JdbcTemplate jdbcTemplate;


	//==================[ 📌insertLedger  ]==================
	@Test
	@DisplayName("선택 정보가 없는 가계부는 저장된다.")
	void 필수_정보만_있어도_저장가능(){
		//given
		Ledger ledger = Ledger.builder()
				.code("code")
				.memberId("UCh11001")
				.date("20250101")
				.category("010101")
				.amount(10000L)
				.build();

		//when
		Long id = repository.insertLedger(ledger);

		//then
		Map<String, Object> result = jdbcTemplate.queryForMap(
				"SELECT code, member_id, transaction_date, fix, payment_type, category_id, amount, created_at FROM ledger WHERE id = ?",
				id
		);

		assertThat(result.get("code")).isEqualTo("code");
		assertThat(result.get("member_id")).isEqualTo("UCh11001");
		assertThat(result.get("transaction_date")).isEqualTo("20250101");
		assertThat(result.get("fix")).isEqualTo("N");
		assertThat(result.get("payment_type")).isEqualTo("NONE");
		assertThat(((Number) result.get("amount")).longValue()).isEqualTo(10000);
		assertThat(result.get("created_at")).isNotNull();
	}

	@Test
	@DisplayName("필수와 선택 정보가 모두 있는 가계부는 저장된다.")
	void 선택_정보_저장가능(){
		//given
		Ledger ledger = Ledger.builder()
				.code("code")
				.memberId("UCh11001")
				.date("20250101")
				.fix(FixedYN.REPEAT)
				.fixCycle(FixedPeriod.MONTHLY)
				.category("010101")
				.amount(10000L)
				.paymentType(PaymentType.CARD)
				.memo("메모")
				.place(
						new Place("강남 CGV", "도로명 주소", "상세 주소 123")
				)
				.createdAt(LocalDateTime.of(2025, 1, 1, 12,45,0))
				.updatedAt(LocalDateTime.of(2025, 1, 5, 12,45,0))
				.build();

		//when
		Long id = repository.insertLedger(ledger);

		//then
		assertThat(id).isNotNull();

		Map<String, Object> result = jdbcTemplate.queryForMap(
				"SELECT * FROM ledger WHERE id = ?",
				id
		);
		assertThat(result.get("fix")).isEqualTo("Y");
		assertThat(result.get("fix_cycle")).isEqualTo("M");
		assertThat(result.get("payment_type")).isEqualTo("CARD");
		assertThat(result.get("memo")).isEqualTo("메모");
		assertThat(result.get("place_name")).isEqualTo("강남 CGV");
		assertThat(result.get("road_address")).isEqualTo("도로명 주소");
		assertThat(result.get("updated_at")).isNotNull();
	}

	@Test
	@DisplayName("가계부 서비스 고유 번호 code가 중복되면 DataIntegrityViolationException이 발생한다.")
	void 가계부_코드가_중복되면_예외발생(){
		//given
		Ledger ledger = Ledger.builder()
				.code("01ARZ3NDEKTSV4RRFFQ69G5FAV")
				.memberId("UCh11001")
				.date("20250101")
				.category("010101")
				.amount(10000L)
				.build();

		//when & then
		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> repository.insertLedger(ledger));
	}

	@ParameterizedTest(name = "[{index}] param={1}")
	@MethodSource("validImportantValue")
	@DisplayName("필수 정보가 null이면 DataIntegrityViolationException이 발생한다.")
	void 가계부_필수정보_없으면_예외발생(Ledger ledger, String type){
		//when & then
		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> repository.insertLedger(ledger));
	}

	static Stream<Arguments> validImportantValue() {
		return Stream.of(
			Arguments.of(
					Ledger.builder()
					.code(null)
					.memberId("UCh11001").date("20250101").category("010101").amount(10000L).build(),
					"code"
			),
			Arguments.of(
					Ledger.builder()
					.memberId(null)
					.code("code").date("20250101").category("010101").amount(10000L).build(),
					"member_id"
			),
			Arguments.of(
					Ledger.builder()
						.date(null)
						.code("code").memberId("UCh11001").category("010101").amount(10000L).build(),
					"date"
			),
			Arguments.of(
					Ledger.builder()
							.category(null)
							.code("code").memberId("UCh11001").date("20250101").amount(10000L).build(),
					"category"
			),
			Arguments.of(
					Ledger.builder()
							.amount(null)
							.code("code").memberId("UCh11001").date("20250101").category("010101").build(),
					"amount"
			)
		);
	}


	//==================[ 📌selectLedgerById  ]==================
	@Test
	@DisplayName("가계부 번호 id로 가게부 정보가 가능하다.")
	void 가계부_번호로_조회가능(){
		//given
		Long id = 1L;

		//when
		Ledger result = repository.selectLedgerById(id);

		//then
		assertThat(result.getPaymentType()).isNotNull();
		assertThat(result.getFix()).isNotNull();
		assertThat(result.getCreatedAt()).isNotNull();

		assertThat(result.getAmount()).isGreaterThan(0);
		assertThat(result.getCategory()).hasSize(6);
		assertThat(result.getDate()).hasSize(8);
	}

	@Test
	@DisplayName("가계부 번호 id가 없으면 EmptyResultDataAccessException이 발생한다.")
	void 가계부_번호가_없으면_예외발생(){
		//given
		Long id = 100L;

		//when & then
		assertThatExceptionOfType(EmptyResultDataAccessException.class)
				.isThrownBy(() -> repository.selectLedgerById(id));
	}

}
