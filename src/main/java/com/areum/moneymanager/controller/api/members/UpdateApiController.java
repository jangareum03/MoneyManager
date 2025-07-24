package com.areum.moneymanager.controller.api.members;

import com.areum.moneymanager.dto.request.member.UpdateRequestDTO;
import com.areum.moneymanager.dto.response.ApiResponseDTO;
import com.areum.moneymanager.exception.ErrorException;
import com.areum.moneymanager.service.member.ImageServiceImpl;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpSession;



/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.controller.api.members<br>
 *  * 파일이름       : UpdateApiController<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원정보 수정 API를 제공하는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>클래스 전체 리팩토링(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@RestController
@RequestMapping("/api/member")
public class UpdateApiController {

	private final Logger logger = LogManager.getLogger(this);

	private final MemberServiceImpl memberService;
	private final ImageServiceImpl imageService;

	public UpdateApiController( MemberServiceImpl memberService, ImageServiceImpl imageService ) {
		this.memberService = memberService;
		this.imageService = imageService;
	}



	/**
	 * 일부 회원정보 수정 요청을 처리합니다.
	 *
	 * @param update			변경할 회원정보
	 * @param session			사용자 식별 및 정보를 저장하는 객체
	 * @return	안내 메시지
	 */
	@PatchMapping
	public ResponseEntity<ApiResponseDTO> updateInfo( @RequestBody UpdateRequestDTO.MemberInfo update, HttpSession session ) {
		try{
			String result = memberService.changeMember( (String)session.getAttribute("mid"), update );

			return ResponseEntity.ok( ApiResponseDTO.builder().success(true).message(result).build() );
		}catch ( ErrorException e ) {
			logger.debug("{} 회원의 정보가 수정 불가합니다.", (String)session.getAttribute("mid"));

			return ResponseEntity.ok( ApiResponseDTO.builder().success(false).message(e.getErrorMessage()).build() );
		}
	}



	/**
	 * 회원 프로필 수정 요청을 처리합니다. <br>
	 *
	 * @param profile				변경할 프로필 이미지
	 * @param session			사용자 식별 및 정보를 저장하는 객체
	 * @return	안내 메시지
	 */
	@PostMapping("/profile")
	public ResponseEntity<ApiResponseDTO> updateProfile( @RequestParam boolean isReset, @RequestParam(required = false) MultipartFile profile, HttpSession session ) {
		String memberId = (String)session.getAttribute("mid");

		memberService.changeProfile( memberId, isReset, profile );

		String profileImageName = imageService.findImage(memberId);
		session.setAttribute("profile", profileImageName);

		return ResponseEntity.ok( ApiResponseDTO.builder().success(true).message(profileImageName).build() );
	}



	/**
	 * 입력한 비밀번호로 변경 요청을 처리합니다.
	 *
	 * @param password 		변경할 비밀번호
	 * @param session 		사용자 식별 및 정보를 저장하는 객체
	 * @return 비밀번호 변경 성공하면 1, 실패하면 0
	 */
	@PostMapping("/password")
	public ResponseEntity<ApiResponseDTO> putUpdatePassword( HttpSession session, @RequestBody UpdateRequestDTO.Password password ) {
		String memberId = (String) session.getAttribute("mid");

		memberService.changePassword( memberId, password.getPassword() );

		return ResponseEntity.ok(ApiResponseDTO.builder().success(true).message("변경한 비밀번호로 로그인해주세요.").build());
	}



	@DeleteMapping
	public ResponseEntity<ApiResponseDTO> deleteMember(HttpSession session, @RequestBody UpdateRequestDTO.Delete delete ) {
		String memberId = (String) session.getAttribute("mid");

		try{
			memberService.deleteMember( memberId, delete );

			return ResponseEntity.ok(ApiResponseDTO.builder().success(true).message("탈퇴가 완료되었습니다.\n그동안 저희 서비스를 이용해주셔서 감사합니다. :)").build());
		}catch ( ErrorException e ) {
			return ResponseEntity.ok( ApiResponseDTO.builder().success(false).message(e.getErrorMessage()).build() );
		}
	}
}
