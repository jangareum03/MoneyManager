package com.moneymanager.unit.service.member;

import com.moneymanager.repository.member.MemberRepository;
import com.moneymanager.service.member.MemberReadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.service.member<br>
 * 파일이름       : MemberReadServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 9<br>
 * 설명              : MemberReadService 클래스 로직을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 1. 9.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
public class MemberReadServiceTest {

	@InjectMocks	private MemberReadService service;

	@Mock	private MemberRepository memberRepository;


	//==================[ 📌getImageLimit  ]==================
	@ParameterizedTest(name = "[{index}] limit={0}")
	@ValueSource(ints = {0, 1, 2, 3})
	@DisplayName("회원이 등록 가능한 이미지 개수를 반환한다.")
	void 등록_가능한_이미지개수_정상범위(int expected){
		//given
		String memberId = "UNn12001";

		when(memberRepository.findImageLimitByMemberId(memberId))
				.thenReturn(expected);

		//when
		int result = service.getImageLimit(memberId);

		assertThat(result).isEqualTo(expected);
	}

	@Test
	@DisplayName("등록 가능한 이미지 개수가 서비스 허용범위보다 크면 허용범위를 반환한다.")
	void 회원_이미지_개수와_서비스_허용범위_비교(){
		//given
		String memberId = "UNn12001";

		when(memberRepository.findImageLimitByMemberId(memberId))
				.thenReturn(10);

		//when
		int result = service.getImageLimit(memberId);

		assertThat(result).isEqualTo(3);
	}

	@Test
	@DisplayName("없는 회원이면 기본값인 0을 반환한다.")
	void 조회결과_없으면_예외발생(){
		//given
		String memberId = "UNn12001";

		when(memberRepository.findImageLimitByMemberId(memberId))
				.thenThrow(EmptyResultDataAccessException.class);

		//when
		int result = service.getImageLimit(memberId);

		assertThat(result).isEqualTo(0);
	}
}
