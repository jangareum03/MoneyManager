package com.moneymanager.unit.repository.ledger;

import com.moneymanager.config.DatabaseConfig;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

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

	@Autowired	private LedgerImageRepository repository;

	@Autowired	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	void setUp() {
		repository.deleteAll();
	}


	//==================[ TEST ]==================
	@Test
	@DisplayName("여러 건의 이미지 정보는 데이터베이스에 저장된다.")
	void saveAll_Success(){
		//given
		List<LedgerImage> images = List.of(
				LedgerImage.builder().ledgerId(1L).imagePath("/img/1.png").sortOrder(1).build(),
				LedgerImage.builder().ledgerId(1L).imagePath("/img/2.png").sortOrder(2).build(),
				LedgerImage.builder().ledgerId(1L).imagePath("/img/3.png").sortOrder(3).build()
		);

		//when
		repository.saveAll(images);

		//then
		Integer result = repository.count();

		assertThat(result).isEqualTo(3);
	}

	@Test
	@DisplayName("빈 이미지 리스트는 아무일도 일어나지 않는다.")
	void 빈_리스트면_무반응() {
		//when
		repository.saveAll(Collections.emptyList());

		//then
		Integer result = repository.count();

		assertThat(result).isZero();
	}

}
