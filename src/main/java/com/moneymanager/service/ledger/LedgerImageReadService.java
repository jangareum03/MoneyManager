package com.moneymanager.service.ledger;

import com.moneymanager.domain.global.Policy;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.member.MemberReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.ledger<br>
 * 파일이름       : LedgerImageReadService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 4. 26<br>
 * 설명              : 가계부 이미지 정보를 조회하는 클래스
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
 * 		 	  <td>26. 4. 26</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class LedgerImageReadService {

	private SecurityUtil securityUtil;

	private final MemberReadService memberReadService;
	private final LedgerImageRepository imageRepository;


	/**
	 * 회원이 사용할 수 있는 가계부 이미지 슬롯 상태를 반환합니다.
	 * <p>
	 *     회원이 등록할 수 있는 최대 이미지 개수({@code maxSlot})를 기준으로,
	 *     실제 사용 가능한 슬롯과 사용 불가능한 슬롯을 {@link Boolean} 값으로 표현합니다.
	 *     사용 가능한 슬롯은 {@code true}로, 사용 불가능한 슬롯은 {@code false}로 표현합니다.
	 * </p>
	 *
	 * @return	이미지 슬롯 사용 가능 여부를 나타내는 Boolean 리스트
	 */
	public List<Boolean> fetchBooleanList(){
		List<Boolean> slotList = new ArrayList<>();

		String memberId = securityUtil.getMemberId();

		int availableSlot = memberReadService.getImageLimit(memberId);
		int maxSlot = Policy.LEDGER_MAX_IMAGE;
		for( int i =0; i < maxSlot; i++ ) {
			slotList.add( i < availableSlot );
		}

		return slotList;
	}


	public List<LedgerImage> getLedgerImages(Long id) {
		return imageRepository.findByLedgerId(id);
	}

}
