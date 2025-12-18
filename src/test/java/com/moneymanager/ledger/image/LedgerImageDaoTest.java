package com.moneymanager.ledger.image;

import com.moneymanager.dao.main.LedgerImageDao;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.image<br>
 * 파일이름       : LedgerImageDaoTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 18<br>
 * 설명              : 가계부 이미지 정보를 DB에서 조회 가능한지 확인하는 테스트 클래스
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
 * 		 	  <td>25. 12. 18.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LedgerImageDaoTest {

	@Autowired
	private LedgerImageDao dao;

	//=================================================
	// findImageListByLedger() 테스트
	//=================================================
	@DisplayName("없는 가계부 ID로 조회 시 빈 리스트로 반환된다.")
	@Test
	void 가계부ID_없으면_빈리스트_반환(){
		//given
		Long id = 1L;

		//when
		List<LedgerImage> result = dao.findImageListByLedger(id);

		//then
		assertThat(result).isEmpty();
	}

	@DisplayName("있는 가계부 ID로 조회 시 해당 ")
	@Test
	void 가계부ID_있으면_리스트_반환(){
		//given
		Long id = 3L;
		List<LedgerImage> mockResult = List.of(
				LedgerImage.builder()
						.id(2L)
						.ledgerId(Ledger.builder().num(id).build())
						.imagePath("/2025/10/15/f47ac10b-58cc-4372-a567-0e02b2c3d479.png")
						.sortOrder(1)
						.createdAt(LocalDateTime.of(2025, 10, 15, 15, 46, 32))
						.updatedAt(null)
						.build(),
				LedgerImage.builder()
						.id(3L)
						.ledgerId(Ledger.builder().num(id).build())
						.imagePath("/2025/10/22/3c2a8f0e-7d6b-4e1c-9f5a-0d4b3c2e1f0d.png")
						.sortOrder(2)
						.createdAt(LocalDateTime.of(2025, 10, 15, 15, 46, 32))
						.updatedAt(LocalDateTime.of(2025, 10, 22, 12, 34, 21))
						.build()
		);

		//when
		List<LedgerImage> result = dao.findImageListByLedger(id);

		//then
		assertThat(result).hasSize(2);
		assertThat(result)
				.usingRecursiveComparison()
				.ignoringFields("createdAt","updatedAt", "ledgerId")
				.isEqualTo(mockResult);
	}

}
