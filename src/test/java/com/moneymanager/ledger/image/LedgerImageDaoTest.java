package com.moneymanager.ledger.image;

import com.moneymanager.dao.main.LedgerImageDao;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

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
	// insertAll() 테스트
	//=================================================
	@DisplayName("없는 가계부 ID로 insert하면 DataIntegrityViolationException 예외가 발생한다.")
	@Test
	void 가계부ID_없으면_실패(){
		//given
		Long id = 13L;
		List<LedgerImage> images =
				List.of(
						LedgerImage.builder()
								.ledgerId(id)
								.imagePath("/temp/a.png")
								.sortOrder(1)
								.build(),
						LedgerImage.builder()
								.ledgerId(id)
								.imagePath("/temp/b.png")
								.sortOrder(2)
								.build()
				);

		//when & then
		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> dao.insertAll(images));
	}

	@DisplayName("정렬 컬럼에 동일한 값을 넣으면 DataIntegrityViolationException 예외가 발생한다.")
	@Test
	void 정렬컬럼_중복되면_예외발생(){
		//given
		 Long id = 1L;
		List<LedgerImage> images =
				List.of(
						LedgerImage.builder()
								.ledgerId(id)
								.imagePath("/temp/a.png")
								.sortOrder(1)
								.build(),
						LedgerImage.builder()
								.ledgerId(id)
								.imagePath("/temp/b.png")
								.sortOrder(1)
								.build()
				);

		//when & then
		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> dao.insertAll(images));
	}

	@DisplayName("모두 정상이면 예외없이 insert한다.")
	@Test
	void 정상값_이면_추가성공() {
		//given
		Long id = 1L;

		List<LedgerImage> images =
				List.of(
						LedgerImage.builder()
								.ledgerId(id)
								.imagePath("/temp/a.png")
								.sortOrder(1)
								.build(),
						LedgerImage.builder()
								.ledgerId(id)
								.imagePath("/temp/b.png")
								.sortOrder(2)
								.build()
				);

		//when
		dao.insertAll(images);
		List<LedgerImage> result = dao.findImageListByLedger(id);

		//then
		assertThat(result)
				.isNotNull()
				.hasSize(2);

		assertThat(result)
				.extracting(LedgerImage::getImagePath)
				.containsExactly("/temp/a.png", "/temp/b.png");

		assertThat(result)
				.extracting(LedgerImage::getSortOrder)
				.containsExactly(1, 2);
	}

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
						.ledgerId(1L)
						.imagePath("/2025/10/15/f47ac10b-58cc-4372-a567-0e02b2c3d479.png")
						.sortOrder(1)
						.createdAt(LocalDateTime.of(2025, 10, 15, 15, 46, 32))
						.updatedAt(null)
						.build(),
				LedgerImage.builder()
						.id(3L)
						.ledgerId(1L)
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


	//=================================================
	// deleteByLedgerId() 테스트
	//=================================================
	@DisplayName("삭제할 데이터가 없으면 결과는 0이다.")
	@Test
	void 이미지ID_없으면_0반환(){
		//given
		Long id = 13L;

		//when
		int result = dao.deleteByLedgerId(id);

		//then
		assertThat(result).isZero();
	}

	@DisplayName("삭제할 데이터가 있으면 결과는 1이다.")
	@ParameterizedTest
	@ValueSource(longs = {2L, 3L})
	void 이미지ID_있으면_1반환(Long id){
		//given
		List<LedgerImage> mockList = List.of(
				LedgerImage.builder()
						.id(2L)
						.ledgerId(1L)
						.imagePath("/2025/10/15/f47ac10b-58cc-4372-a567-0e02b2c3d479.png")
						.sortOrder(1)
						.createdAt(LocalDateTime.of(2025, 10, 15, 15, 46, 32))
						.updatedAt(null)
						.build(),
				LedgerImage.builder()
						.id(3L)
						.ledgerId(1L)
						.imagePath("/2025/10/22/3c2a8f0e-7d6b-4e1c-9f5a-0d4b3c2e1f0d.png")
						.sortOrder(2)
						.createdAt(LocalDateTime.of(2025, 10, 15, 15, 46, 32))
						.updatedAt(LocalDateTime.of(2025, 10, 22, 12, 34, 21))
						.build()
		);

		dao.insertAll(mockList);

		//when
		int result = dao.deleteByLedgerId(id);

		//then
		assertThat(result).isEqualTo(1);
	}
}
