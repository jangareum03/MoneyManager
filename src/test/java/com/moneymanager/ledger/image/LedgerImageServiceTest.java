package com.moneymanager.ledger.image;

import com.moneymanager.dao.main.LedgerImageDao;
import com.moneymanager.dao.member.MemberInfoDaoImpl;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.service.main.ImageServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.image<br>
 * 파일이름       : LedgerImageServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 10<br>
 * 설명              : 가계부 이미지와 관련된 기능을 확인하는 테스트 클래스
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
 * 		 	  <td>25. 12. 10.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
	public class LedgerImageServiceTest {

	@InjectMocks	private ImageServiceImpl imageService;

	@Mock				private LedgerImageDao imageDao;
	@Mock				private MemberInfoDaoImpl memberInfoDao;


	//=================================================
	// getLimitImageCount() 테스트
	//=================================================
	@DisplayName("회원번호가 없으면 기본값으로 반환한다.")
	@Test
	void 회원번호_없으면_기본값반환(){
		//given
		String memberId = "not";
		when(memberInfoDao.findImageLimit(memberId)).thenThrow(EmptyResultDataAccessException.class);

		//when
		int result = imageService.getLimitImageCount(memberId);

		//then
		assertThat(result).isEqualTo(1);
	}

	@DisplayName("회원번호가 있으면 설정된 개수를 반환한다.")
	@Test
	void 회원번호_있으면_개수반환(){
		//given
		String memberId = "member";
		when(memberInfoDao.findImageLimit(memberId)).thenReturn(3);

		//when
		int result = imageService.getLimitImageCount(memberId);

		//then
		assertThat(result).isEqualTo(3);
	}



	//=================================================
	// getImageListByLedger() 테스트
	//=================================================
	@DisplayName("없는 가계부는 모든 요소가 Null인 리스트를 반환한다.")
	@Test
	void 가계부에_이미지_없으면_null리스트_반환(){
		//given
		Long id = 5L;
		when(imageDao.findImageListByLedger(id)).thenReturn(Collections.emptyList());

		//when
		List<LedgerImage> result = imageService.getImageListByLedger(id, 1);

		//then
		assertThat(result).hasSize(3);
		assertThat(result).allMatch(Objects::isNull);
	}

	@DisplayName("가계부에 저장된 이미지 리스트를 반환한다.")
	@Test
	void 가계부에_이미지_있으면_File리스트_반환(){
		//given
		Long id = 2L;
		when(imageDao.findImageListByLedger(id))
				.thenReturn(
						List.of(
								LedgerImage.builder()
										.id(1L)
										.ledgerId(Ledger.builder().id("ledger1").build())
										.imagePath("/2025/11/23/b8d3c9a1-4f8e-4a7b-8c6d-5e9f2a1b0c4d.png")
										.sortOrder(2)
										.createdAt(LocalDateTime.of(2025, 11, 23, 11,11,11))
										.build(),
								LedgerImage.builder()
										.id(2L)
										.ledgerId(Ledger.builder().id("ledger1").build())
										.imagePath("/2025/11/25/3c2a8f0e-7d6b-4e1c-9f5a-0d4b3c2e1f0d.jpg")
										.sortOrder(1)
										.createdAt(LocalDateTime.of(2025, 11, 23, 11,11,11))
										.updatedAt(LocalDateTime.of(2025, 11, 25, 11,11,11))
										.build()
						)
				);

		List<LedgerImage> mockResult = new ArrayList<>();
		mockResult.add(
				LedgerImage.builder()
						.id(2L)
						.ledgerId(Ledger.builder().id("ledger1").build())
						.imagePath("/2025/11/25/3c2a8f0e-7d6b-4e1c-9f5a-0d4b3c2e1f0d.jpg")
						.sortOrder(1)
						.createdAt(LocalDateTime.of(2025, 11, 23, 11,11,11))
						.updatedAt(LocalDateTime.of(2025, 11, 25, 11,11,11))
						.build()
		);
		mockResult.add(
				LedgerImage.builder()
						.id(1L)
						.ledgerId(Ledger.builder().id("ledger1").build())
						.imagePath("/2025/11/23/b8d3c9a1-4f8e-4a7b-8c6d-5e9f2a1b0c4d.png")
						.sortOrder(2)
						.createdAt(LocalDateTime.of(2025, 11, 23, 11,11,11))
						.build()
		);
		mockResult.add(null);

		//when
		List<LedgerImage> result = imageService.getImageListByLedger(id, 2);

		//then
		assertThat(result).hasSize(3);

		assertThat(result.get(0)).usingRecursiveComparison()
				.isEqualTo(mockResult.get(0));

		assertThat(result.get(1)).usingRecursiveComparison()
				.isEqualTo(mockResult.get(1));

		assertThat(result.get(2)).isNull();
	}



	//=================================================
	// getImageSlots() 테스트
	//=================================================
	@DisplayName("이미지 슬롯은 최소 1개부터 최대 3개까지 사용 가능하다.")
	@ParameterizedTest
	@CsvSource({
			"1, true, false, false",
			"2, true, true, false",
			"3, true, true, true",
	})
	void 이미지슬롯_경계값(int limit, boolean first, boolean second, boolean third){
		//given
		String memberId = "member123";
		when(memberInfoDao.findImageLimit(memberId)).thenReturn(limit);

		//when
		List<Boolean> result = imageService.getImageSlots(memberId);

		//then
		assertThat(result)
				.isNotNull()
				.hasSize(3)
				.containsExactly(first, second, third);
	}
}