package com.moneymanager.service.member;

import com.moneymanager.domain.global.Policy;
import com.moneymanager.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.member<br>
 * 파일이름       : MemberReadService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 8<br>
 * 설명              : 회원 정보를 조회하는 클래스
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
 * 		 	  <td>26. 1. 8.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class MemberReadService {

	private final MemberRepository memberRepository;

	/**
	 * 회원이 한 가계부에 등록할 수 있는 이미지 최대 허용 개수를 계산하여 반환합니다.
	 * <p>
	 *     데이터베이스에서 회원의 이미지 제한값을 조회한 뒤, 시스템에서 지정한 최대 허용치({@code maxImage})와 비교하여 더 작은 값을 반환합니다.
	 *     만약 회원이 존재하지 않아 데이터베이스에서 조회를 할 수 없다면 기본값인 0이 반환됩니다.
	 * </p>
	 *
	 * @param memberId		조회할 회원 ID
	 * @return	이미지 업로드 가능한 이미지 개수
	 */
	public int getImageLimit(String memberId) {
		try {
			Integer hasImage = memberRepository.findImageLimitByMemberId(memberId);
			int maxImage = Policy.LEDGER_MAX_IMAGE;

			return Math.min(hasImage, maxImage);
		}catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}
}
