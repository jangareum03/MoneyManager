package com.areum.moneymanager.controller.api.members;

import com.areum.moneymanager.dto.request.member.UpdateRequestDTO;
import com.areum.moneymanager.dto.response.ApiResponseDTO;
import com.areum.moneymanager.dto.response.ValidationResponseDTO;
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
 * 회원정보 수정을 담당하는 클래스</br>
 * 이름, 비밀번호, 이메일 등의 정보 수정을 처리
 *
 * @version 1.0
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
	public ResponseEntity<ApiResponseDTO> updateProfile(@RequestParam boolean isReset, @RequestParam(required = false) MultipartFile profile, HttpSession session ) {
		String memberId = (String)session.getAttribute("mid");

		try{
			memberService.changeProfile( memberId, isReset, profile );

			String profileImageName = imageService.findImage(memberId);
			session.setAttribute("profile", profileImageName);

			return ResponseEntity.ok( ApiResponseDTO.builder().success(true).message(profileImageName).build() );
		}catch ( ErrorException e ) {
			logger.debug("{} 회원의 프로필이 수정 불가합니다.", memberId);

			return ResponseEntity.ok( ApiResponseDTO.builder().success(false).message(e.getErrorMessage()).build() );
		}
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
		logger.debug("비밀번호: {}", password.getPassword());
		String memberId = (String) session.getAttribute("mid");

		try{
			memberService.changePassword( memberId, password.getPassword() );

			return ResponseEntity.ok(ApiResponseDTO.builder().success(true).message("변경한 비밀번호로 로그인해주세요.").build());
		}catch ( ErrorException e ) {
			logger.debug("{} 회원의 비밀번호가 변경 불가능합니다.", memberId);

			return ResponseEntity.ok(ApiResponseDTO.builder().success(false).message(e.getErrorMessage()).build());
		}
	}



	@DeleteMapping
	public ResponseEntity<ApiResponseDTO> deleteMember(HttpSession session, @RequestBody UpdateRequestDTO.Delete delete ) {
		logger.debug("🍒아이디: {}, 비밀번호: {}, 코드: {}, 원인: {}", delete.getId(), delete.getPassword(), delete.getCode(), delete.getCause());
		String memberId = (String) session.getAttribute("mid");

		try{
			memberService.deleteMember( memberId, delete );

			return ResponseEntity.ok(ApiResponseDTO.builder().success(true).message("탈퇴가 완료되었습니다.\n그동안 저희 서비스를 이용해주셔서 감사합니다. :)").build());
		}catch ( ErrorException e ) {
			return ResponseEntity.ok( ApiResponseDTO.builder().success(false).message(e.getErrorMessage()).build() );
		}
	}
}
