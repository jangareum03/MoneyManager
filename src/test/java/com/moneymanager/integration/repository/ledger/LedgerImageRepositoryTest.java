package com.moneymanager.integration.repository.ledger;

import com.moneymanager.config.DatabaseConfig;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.repository.ledger<br>
 * 파일이름       : LedgerImageRepositoryTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 13<br>
 * 설명              : 가계부 이미지와 관련된 테이블의 데이터 조작 기능을 검증하는 클래스
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
 * 		 	  <td>26. 1. 13.</td>
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
		LedgerImageRepository.class
})
public class LedgerImageRepositoryTest {

	@Autowired
	private LedgerImageRepository repository;

	@Autowired
	private JdbcTemplate jdbcTemplate;


	@Nested
	@Sql(statements = "DELETE FROM ledger_image", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@DisplayName("가계부이미지 저장")
	class SaveTest {

		@Test
		@DisplayName("여러 건의 이미지 정보는 데이터베이스에 반영된다.")
		void savesLedgerImages_whenLedgerImageListIsValid(){
			//given
			LedgerImage d1 = ledgerImage(1L, "/img/1.png", 1);
			LedgerImage d2 = ledgerImage(1L, "/img/2.png", 2);
			LedgerImage d3 = ledgerImage(1L, "/img/3.png", 3);

			List<LedgerImage> images = List.of(d1, d2, d3);

			//when
			repository.saveAll(images);

			//then
			Integer result = repository.count();

			assertThat(result).isEqualTo(3);
		}

		@Test
		@DisplayName("빈 이미지 리스트는 데이터베이스에 반영되지 않는다.")
		void doesNothing_whenLedgerImageListIsEmpty() {
			//when
			repository.saveAll(Collections.emptyList());

			//when
			Integer result = repository.count();

			//then
			assertThat(result).isZero();
		}

	}


	@Nested
	@DisplayName("가계부이미지 조회")
	class FindTest {

		@Test
		@DisplayName("저장된 가계부이미지가 모두 조회횐다.")
		void returnsAllLedgerImages_whenLedgerImagesExist() {
			//when
			List<LedgerImage> result = repository.findAll();

			//then
			assertThat(result).hasSize(3);
		}

		@Test
		@Sql(statements = "DELETE FROM ledger_image", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
		@DisplayName("저장된 가계부이미지가 없으면 빈 리스트가 반환된다.")
		void returnsEmptyList_whenNoLedgerImages() {
			//when
			List<LedgerImage> result = repository.findAll();

			//then
			assertThat(result).hasSize(0);
		}

		@Test
		@DisplayName("저장된 가계부이미지 총 개수를 조회한다.")
		void returnsLedgerImageCount_whenLedgerImagesExist() {
			//when
			Integer result = repository.count();

			//then
			assertThat(result).isEqualTo(3);
		}

		@Test
		@Sql(statements = "DELETE FROM ledger_image", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
		@DisplayName("저장된 가계부이미지가 없으면 개수는 0이다.")
		void returnsZero_whenNoLedgerImagesExist() {
			//when
			Integer result = repository.count();

			//then
			assertThat(result).isZero();
		}

		@Test
		@DisplayName("저장된 가계부이미지를 모두 삭제한다.")
		void returnsZero_whenAllLedgerImagesAreDeleted() {
			//given
			LedgerImage d1 = ledgerImage(5L, "/img/1.png", 1);
			LedgerImage d2 = ledgerImage(5L, "/img/2.png", 2);
			LedgerImage d3 = ledgerImage(5L, "/img/3.png", 3);

			List<LedgerImage> images = List.of(d1, d2, d3);

			repository.saveAll(images);

			//when
			repository.deleteAll();
			Integer result = repository.count();

			//then
			assertThat(result).isZero();
		}

	}


	private LedgerImage ledgerImage(Long ledgerId, String imagePath, int sorted) {
		return LedgerImage.builder()
				.ledgerId(ledgerId)
				.imagePath(imagePath)
				.sortOrder(sorted)
				.build();
	}
}
