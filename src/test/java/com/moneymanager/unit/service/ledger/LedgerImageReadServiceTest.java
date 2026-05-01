package com.moneymanager.unit.service.ledger;

import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.ledger.LedgerImageReadService;
import com.moneymanager.service.member.MemberReadService;
import com.moneymanager.fixture.ledger.LedgerImageFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.service.ledger<br>
 * 파일이름       : LedgerImageReadServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 26.<br>
 * 설명              : LedgerImageReadService 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 4. 26.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
public class LedgerImageReadServiceTest {

	@InjectMocks
	private LedgerImageReadService target;

	@Mock	private SecurityUtil securityUtil;
	@Mock	private MemberReadService memberReadService;
	@Mock	private LedgerImageRepository imageRepository;

	@Nested
	@DisplayName("이미지 슬롯 생성")
	class ImageSlotCreateTest {

		@Test
		@DisplayName("기본적으로 1개의 슬롯만 true다.")
		void returnsBooleanList_whenDefaultMember() {
			//given
			when(securityUtil.getMemberId()).thenReturn("member");
			when(memberReadService.getImageLimit("member")).thenReturn(1);

			//when
			List<Boolean> result = target.fetchBooleanList();

			//then
			assertThat(result).hasSize(3);
			assertThat(result).containsExactly(true, false, false);
		}

		@Test
		@DisplayName("2개의 슬롯만 true이 가능하다.")
		void returnsBooleanList_whenTwoSlots() {
			//given
			when(securityUtil.getMemberId()).thenReturn("member");
			when(memberReadService.getImageLimit("member")).thenReturn(2);

			//when
			List<Boolean> result = target.fetchBooleanList();

			//then
			assertThat(result).hasSize(3);
			assertThat(result).containsExactly(true, true, false);
		}

		@Test
		@DisplayName("최대 슬롯인 3개까지 true가 가능하다.")
		void returnsBooleanList_whenMaxSlots() {
			//given
			when(securityUtil.getMemberId()).thenReturn("member");
			when(memberReadService.getImageLimit("member")).thenReturn(3);

			//when
			List<Boolean> result = target.fetchBooleanList();

			//then
			assertThat(result).hasSize(3);
			assertThat(result).containsExactly(true, true, true);
		}

		@Test
		@DisplayName("회원의 허용범위가 3개보다 커도 최대 슬롯인 3개까지 true가 가능하다.")
		void returnsBooleanList_whenSlotOutOfRange() {
			//given
			when(securityUtil.getMemberId()).thenReturn("member");
			when(memberReadService.getImageLimit("member")).thenReturn(5);

			//when
			List<Boolean> result = target.fetchBooleanList();

			//then
			assertThat(result).hasSize(3);
			assertThat(result).containsExactly(true, true, true);
		}

	}


	@Nested
	@DisplayName("이미지리스트 조회")
	class ImageListTest {

		@Test
		@DisplayName("DB 데이터를 그대로 반환한다.")
		void returnsRepositoryList_whenLedgerIdIsValid() {
			//given
			List<LedgerImage> expected = List.of(
					LedgerImageFixture.defaultImage(1L, 1L).build(),
					LedgerImageFixture.defaultImage(2L, 1L).build()
			);
			when(imageRepository.findByLedgerId(1L)).thenReturn(expected);

			//when
			List<LedgerImage> result = target.getLedgerImages(1L);

			//then
			assertThat(result).isEqualTo(expected);

			verify(imageRepository).findByLedgerId(1L);
		}

	}

}
