package com.areum.moneymanager.controller.api.members.validation;

import com.areum.moneymanager.dto.common.ValidationResultDTO;
import com.areum.moneymanager.dto.member.validation.*;
import com.areum.moneymanager.exception.ErrorException;
import com.areum.moneymanager.service.member.MailService;
import com.areum.moneymanager.service.member.validation.MemberValidationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.controller.api.members<br>
 *  * 파일이름       : MemberValidationApiController<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원 관련 검증 API를 제공하는 클래스
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
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */

@RestController
@RequestMapping("/api/members")
public class MemberValidationApiController {

	private final Logger logger = LogManager.getLogger(this);

	private final MemberValidationService validationService;
	private final MailService mailService;

	public MemberValidationApiController(MemberValidationService verifyService, MailService mailService ) {
		this.validationService = verifyService;
		this.mailService = mailService;
	}



	/**
	 * 회원 아이디 형식이 적합한지 확인 요청을 처리합니다. <br>
	 *
	 * @param checkDTO 		검증할 정보를 담은 객체
	 * @return 검증 결과를 담은 객체
	 */
	@PostMapping("/validate/id")
	public ResponseEntity<ValidationResultDTO> validateId(@RequestBody IdCheckDTO checkDTO ) {
		try{
			MemberValidationService.checkIdAvailability(checkDTO.getId());

			return ResponseEntity.ok(ValidationResultDTO.builder().success(true).build());
		}catch ( ErrorException e ) {
			logger.debug("{} 아이디는 형식이 부적합합니다. ({}: {})", checkDTO.getId(), e.getErrorCode(), e.getErrorMessage());

			return ResponseEntity.ok(ValidationResultDTO.builder().success(false).message(e.getErrorMessage()).build());
		}
	}



	/**
	 * 회원 아이디의 사용 가능 여부 확인 요청을 처리합니다. <br>
	 *
	 * @param checkDTO		검증할 정보를 담은 객체
	 * @return	검증 결과를 담은 객체
	 */
	@PostMapping("/check/id")
	public ResponseEntity<ValidationResultDTO> checkId(@RequestBody IdCheckDTO checkDTO ) {
		try{
			validationService.checkIdExistence(checkDTO.getId());

			return ResponseEntity.ok(ValidationResultDTO.builder().success(true).message("사용 가능한 아이디입니다.").build());
		}catch ( ErrorException e ) {
			logger.debug("{} 아이디는 사용 불가능합니다. {{}: {}}", checkDTO.getId(), e.getErrorCode(), e.getErrorMessage());
			return ResponseEntity.ok(ValidationResultDTO.builder().success(false).message(e.getErrorMessage()).build());
		}
	}



	/**
	 * 회원 비밀번호 형식이 적합한지 확인 요청을 처리합니다. <br>
	 *
	 * @param checkDTO 		검증할 정보를 담은 객체
	 * @return 검증 결과를 담은 객체
	 */
	@PostMapping("/validate/password")
	public ResponseEntity<ValidationResultDTO> validatePwd(@RequestBody PasswordCheckDTO checkDTO ) {
		try{
			MemberValidationService.checkPasswordAvailability( checkDTO.getPassword() );

			return ResponseEntity.ok(ValidationResultDTO.builder().success(true).build());
		}catch ( ErrorException e ) {
			logger.debug("'{}' 비밀번호는 형식이 부적합합니다. ({}: {})", checkDTO.getPassword(), e.getErrorCode(), e.getErrorMessage());
			return ResponseEntity.ok(ValidationResultDTO.builder().success(false).message(e.getErrorMessage()).build() );
		}
	}



	/**
	 * 회원 비밀번호를 정확하게 입력했는지 확인 요청을 처리합니다.
	 *
	 * @param session				사용자 식별 및 정보를 저장하는 객체
	 * @param checkDTO			 검증할 정보를 담은 객체
	 * @return	검증 결과를 담은 객체
	 */
	@PostMapping("/check/password")
	public ResponseEntity<ValidationResultDTO> checkPwd(HttpSession session, @RequestBody PasswordCheckDTO checkDTO) {
		try{
			validationService.checkPasswordIdentify( (String)session.getAttribute("mid"), checkDTO.getPassword() );

			return ResponseEntity.ok(ValidationResultDTO.builder().success(true).message("현재 비밀번호와 일치합니다.").build());
		}catch( ErrorException e ) {
			logger.debug("{} 비밀번호는 현재 비밀번호와 일치하지 않습니다.", checkDTO.getPassword());
			return ResponseEntity.ok( ValidationResultDTO.builder().success(false).message(e.getErrorMessage()).build() );
		}
	}



	/**
	 * 회원 이름 형식이 적합한지 확인 요청을 처리합니다.<br>
	 *
	 * @param checkDTO 		검증할 정보를 담은 객체
	 * @return 검증 결과를 담은 객체
	 */
	@PostMapping("/validate/name")
	public ResponseEntity<ValidationResultDTO> validateName(@RequestBody NameCheckDTO checkDTO ) {
		try{
			MemberValidationService.checkNameAvailability( checkDTO.getName() );

			return ResponseEntity.ok( ValidationResultDTO.builder().success(true).build() );
		}catch ( ErrorException e ) {
			logger.debug("{} 이름은 형식이 부적합합니다. ({}: {})", checkDTO.getName(), e.getErrorCode(), e.getErrorMessage());

			return ResponseEntity.ok().body(ValidationResultDTO.builder().success(false).message(e.getErrorMessage()).build());
		}
	}



	/**
	 * 회원 생년월일 형식이 적합한지 확인 요청을 처리합니다.
	 *
	 * @param checkDTO		검증할 정보를 담은 객체
	 * @return	검증 결과를 담은 객체
	 */
	@PostMapping("/validate/birth")
	public ResponseEntity<ValidationResultDTO> validateBirth(@RequestBody BirthCheckDTO checkDTO ) {
		try{
			MemberValidationService.checkBirthAvailability(checkDTO.getDate());

			return ResponseEntity.ok(ValidationResultDTO.builder().success(true).build());
		}catch ( ErrorException e ) {
			logger.debug("{} 생년월일은 사용 불가능합니다.", checkDTO.getDate());

			return ResponseEntity.ok(ValidationResultDTO.builder().success(false).message(e.getErrorMessage()).build());
		}
	}



	/**
	 * 회원 닉네임이 사용 가능 여부 확인 요청을 처리합니다.<br>
	 *
	 * @param checkDTO		검증할 정보를 담은 객체
	 * @return	검증 결과를 담은 객체
	 */
	@PostMapping("/check/nickname")
	public ResponseEntity<ValidationResultDTO> checkNickname(@RequestBody NicknameCheckDTO checkDTO ) {
		try {
			validationService.checkNicknameExistence(checkDTO.getNickname());

			return ResponseEntity.ok(ValidationResultDTO.builder().success(true).message("사용 가능한 닉네임입니다.").build());
		}catch ( ErrorException e ) {
			logger.debug("{} 닉네임은 사용 불가능합니다. ({}: {})", checkDTO.getNickname(), e.getErrorCode(), e.getErrorMessage());
			return ResponseEntity.ok(ValidationResultDTO.builder().success(false).message(e.getErrorMessage()).build());
		}
	}



	/**
	 * 회원 이메일 형식이 적합한지 확인 요청을 처리합니다.<br>
	 *
	 * @param checkDTO 		검증할 정보를 담은 객체
	 * @return 검증 결과를 담은 객체
	 */
	@PostMapping("/validate/email")
	public ResponseEntity<ValidationResultDTO> validateEmail(@RequestBody EmailCheckDTO checkDTO ) {
		try{
			MemberValidationService.checkEmailAvailability(checkDTO.getEmail());

			return ResponseEntity.ok(ValidationResultDTO.builder().success(true).build());
		}catch ( ErrorException e ) {
			logger.debug("{} 이메일은 형식이 부적합합니다. ({}: {})", checkDTO.getEmail(), e.getErrorCode(), e.getErrorMessage());
			return ResponseEntity.ok(ValidationResultDTO.builder().success(false).message(e.getErrorMessage()).build());
		}
	}



	/**
	 * 회원 이메일의 사용 가능 여부 확인 요청을 처리합니다. <br>
	 *
	 * @param session			사용자 식별 및 정보를 저장하는 객체
	 * @param checkDTO		검증할 정보를 담은 객체
	 * @return	검증 결과를 담은 객체
	 */
	@PostMapping("/check/email")
	public ResponseEntity<ValidationResultDTO> checkEmail(HttpSession session, @RequestBody EmailCheckDTO checkDTO) {
		try{
			String code = mailService.send("email", null, checkDTO.getEmail());

			logger.debug("{} 이메일로 보낸 인증코드는 [ {} ] 입니다.", checkDTO.getEmail(), code);
			if(Objects.nonNull(code) ) {	//이메일 전송 성공
				session.setAttribute(checkDTO.getEmail(), code);
			}

			return ResponseEntity.ok(ValidationResultDTO.builder().success(true).message("작성한 이메일로 인증코드 전송했습니다.").build());
		}catch ( ErrorException e ) {
			logger.debug("{} 이메일은 사용 불가능합니다. ({}: {})", checkDTO.getEmail(), e.getErrorCode(), e.getErrorMessage());
			return ResponseEntity.ok( ValidationResultDTO.builder().success(false).message(e.getErrorMessage()).build() );
		}
	}



	/**
	 * 회원 이메일 인증코드 일치 여부 확인 요청을 처리합니다.<br>
	 *
	 * @param request				서버에 전송한 HTTP 요청 객체
	 * @param checkDTO			검증할 정보를 담은 객체
	 * @return	검증 결과를 담은 객체
	 */
	@PostMapping("/check/email-code")
	public ResponseEntity<ValidationResultDTO> validateEmailCode(HttpServletRequest request, @RequestBody EmailCheckDTO checkDTO) {
		try{
			validationService.checkEmailCode( request.getSession(), checkDTO );

			return ResponseEntity.ok(ValidationResultDTO.builder().success(true).message("이메일 인증 완료했습니다.").build());
		}catch ( ErrorException e ) {
			logger.debug("{} 이메일로 전송한 인증코드와 불일치합니다. ({}: {})", checkDTO.getEmail(), e.getErrorCode(), e.getErrorMessage());
			return ResponseEntity.ok( ValidationResultDTO.builder().success(false).message(e.getErrorMessage()).build() );
		}
	}


}
