package com.moneymanager.ledger.image;

import com.moneymanager.dao.main.LedgerDao;
import com.moneymanager.dao.member.MemberInfoDaoImpl;
import com.moneymanager.service.main.ImageServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

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

	@Mock				private LedgerDao ledgerDao;
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

}
