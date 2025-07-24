package com.areum.moneymanager.controller.api.members.validation;

import com.areum.moneymanager.dto.request.member.validation.MemberRequestDTO;
import com.areum.moneymanager.dto.response.ValidationResponseDTO;
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
 *		 	  <td>클래스 전체 리팩토링(버전 2.0)</td>
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
	 * @param id 		아이디
	 * @return 안내 메시지
	 */
	@PostMapping("/validate/id")
	public ResponseEntity<ValidationResponseDTO> validateId( @RequestBody MemberRequestDTO.Id id ) {
		try{
			MemberValidationService.checkIdAvailability(id.getId());

			return ResponseEntity.ok(ValidationResponseDTO.builder().success(true).build());
		}catch ( ErrorException e ) {
			logger.debug("{} 아이디는 형식이 부적합합니다. ({}: {})", id.getId(), e.getErrorCode(), e.getErrorMessage());

			return ResponseEntity.ok(ValidationResponseDTO.builder().success(false).message(e.getErrorMessage()).build());
		}
	}



	/**
	 * 회원 아이디의 사용 가능 여부 확인 요청을 처리합니다. <br>
	 *
		 * @param id		아이디
	 * @return	안내 메시지
	 */
	@PostMapping("/check/id")
	public ResponseEntity<ValidationResponseDTO> checkId( @RequestBody MemberRequestDTO.Id id ) {
		try{
			validationService.checkIdExistence(id.getId());

			return ResponseEntity.ok(ValidationResponseDTO.builder().success(true).message("사용 가능한 아이디입니다.").build());
		}catch ( ErrorException e ) {
			logger.debug("{} 아이디는 사용 불가능합니다. {{}: {}}", id.getId(), e.getErrorCode(), e.getErrorMessage());
			return ResponseEntity.ok(ValidationResponseDTO.builder().success(false).message(e.getErrorMessage()).build());
		}
	}



	/**
	 * 회원 비밀번호 형식이 적합한지 확인 요청을 처리합니다. <br>
	 *
	 * @param password 		비밀번호
	 * @return 안내 메시지
	 */
	@PostMapping("/validate/password")
	public ResponseEntity<ValidationResponseDTO> validatePwd( @RequestBody MemberRequestDTO.Password password ) {
		try{
			MemberValidationService.checkPasswordAvailability( password.getPassword() );

			return ResponseEntity.ok(ValidationResponseDTO.builder().success(true).build());
		}catch ( ErrorException e ) {
			logger.debug("'{}' 비밀번호는 형식이 부적합합니다. ({}: {})", password.getPassword(), e.getErrorCode(), e.getErrorMessage());
			return ResponseEntity.ok(ValidationResponseDTO.builder().success(false).message(e.getErrorMessage()).build() );
		}
	}



	/**
	 * 회원 비밀번호를 정확하게 입력했는지 확인 요청을 처리합니다.
	 *
	 * @param password		비밀번호
	 * @param session				사용자 식별 및 정보를 저장하는 객체
	 * @return	안내 메시지
	 */
	@PostMapping("/check/password")
	public ResponseEntity<ValidationResponseDTO> checkPwd(@RequestBody MemberRequestDTO.Password password, HttpSession session ) {
		try{
			validationService.checkPasswordIdentify( (String)session.getAttribute("mid"), password.getPassword() );

			return ResponseEntity.ok(ValidationResponseDTO.builder().success(true).message("현재 비밀번호와 일치합니다.").build());
		}catch( ErrorException e ) {
			logger.debug("{} 비밀번호는 현재 비밀번호와 일치하지 않습니다.", password.getPassword());
			return ResponseEntity.ok( ValidationResponseDTO.builder().success(false).message(e.getErrorMessage()).build() );
		}
	}



	/**
	 * 회원 이름 형식이 적합한지 확인 요청을 처리합니다.<br>
	 *
	 * @param name 		형식을 확인할 이름
	 * @return 안내 메시지
	 */
	@PostMapping("/validate/name")
	public ResponseEntity<ValidationResponseDTO> validateName(@RequestBody MemberRequestDTO.Name name ) {
		try{
			MemberValidationService.checkNameAvailability( name.getName() );

			return ResponseEntity.ok( ValidationResponseDTO.builder().success(true).build() );
		}catch ( ErrorException e ) {
			logger.debug("{} 이름은 형식이 부적합합니다. ({}: {})", name.getName(), e.getErrorCode(), e.getErrorMessage());

			return ResponseEntity.ok().body(ValidationResponseDTO.builder().success(false).message(e.getErrorMessage()).build());
		}
	}


	@PostMapping("/validate/birth")
	public ResponseEntity<ValidationResponseDTO> validateBirth( @RequestBody MemberRequestDTO.Birth birth ) {
		try{
			MemberValidationService.checkBirthAvailability(birth.getBirthDate());

			return ResponseEntity.ok(ValidationResponseDTO.builder().success(true).build());
		}catch ( ErrorException e ) {
			logger.debug("{} 생년월일은 사용 불가능합니다.", birth.getBirthDate());

			return ResponseEntity.ok(ValidationResponseDTO.builder().success(false).message(e.getErrorMessage()).build());
		}
	}


	/**
	 * 회원 닉네임이 사용 가능 여부 확인 요청을 처리합니다.<br>
	 *
	 * @param nickname		닉네임
	 * @return	안내 메시지
	 */
	@PostMapping("/check/nickname")
	public ResponseEntity<ValidationResponseDTO> checkNickname( @RequestBody MemberRequestDTO.Nickname nickname ) {
		try {
			validationService.checkNicknameExistence(nickname.getNickName());

			return ResponseEntity.ok(ValidationResponseDTO.builder().success(true).message("사용 가능한 닉네임입니다.").build());
		}catch ( ErrorException e ) {
			logger.debug("{} 닉네임은 사용 불가능합니다. ({}: {})", nickname.getNickName(), e.getErrorCode(), e.getErrorMessage());
			return ResponseEntity.ok(ValidationResponseDTO.builder().success(false).message(e.getErrorMessage()).build());
		}
	}



	/**
	 * 회원 이메일 형식이 적합한지 확인 요청을 처리합니다.<br>
	 *
	 * @param email 		이메일
	 * @return 안내 메시지
	 */
	@PostMapping("/validate/email")
	public ResponseEntity<ValidationResponseDTO> validateEmail( @RequestBody MemberRequestDTO.Email email ) {
		try{
			MemberValidationService.checkEmailAvailability(email.getEmail());

			return ResponseEntity.ok(ValidationResponseDTO.builder().success(true).build());
		}catch ( ErrorException e ) {
			logger.debug("{} 이메일은 형식이 부적합합니다. ({}: {})", email.getEmail(), e.getErrorCode(), e.getErrorMessage());
			return ResponseEntity.ok(ValidationResponseDTO.builder().success(false).message(e.getErrorMessage()).build());
		}
	}



	/**
	 * 회원 이메일의 사용 가능 여부 확인 요청을 처리합니다. <br>
	 *
	 * @param email		이메일
	 * @return	안내 메시지
	 */
	@PostMapping("/check/email")
	public ResponseEntity<ValidationResponseDTO> checkEmail(@RequestBody MemberRequestDTO.Email email, HttpSession session ) {
		try{
			String code = mailService.send("email", null, email.getEmail());

			logger.debug("{} 이메일로 보낸 인증코드는 [ {} ] 입니다.", email.getEmail(), code);
			if(Objects.nonNull(code) ) {	//이메일 전송 성공
				session.setAttribute(email.getEmail(), code);
			}

			return ResponseEntity.ok(ValidationResponseDTO.builder().success(true).message("작성한 이메일로 인증코드 전송했습니다.").build());
		}catch ( ErrorException e ) {
			logger.debug("{} 이메일은 사용 불가능합니다. ({}: {})", email.getEmail(), e.getErrorCode(), e.getErrorMessage());
			return ResponseEntity.ok( ValidationResponseDTO.builder().success(false).message(e.getErrorMessage()).build() );
		}
	}



	/**
	 * 회원 이메일 인증코드 일치 여부 확인 요청을 처리합니다.<br>
	 *
	 * @param code		인증코드
	 * @return	안내 메시지
	 */
	@PostMapping("/check/email-code")
	public ResponseEntity<ValidationResponseDTO> validateEmailCode(@RequestBody MemberRequestDTO.EmailCodeCheck code, HttpServletRequest request ) {
		try{
			validationService.checkEmailCode( request.getSession(), code );

			return ResponseEntity.ok(ValidationResponseDTO.builder().success(true).message("이메일 인증 완료했습니다.").build());
		}catch ( ErrorException e ) {
			logger.debug("{} 이메일로 전송한 인증코드와 불일치합니다. ({}: {})", code.getEmail(), e.getErrorCode(), e.getErrorMessage());
			return ResponseEntity.ok( ValidationResponseDTO.builder().success(false).message(e.getErrorMessage()).build() );
		}
	}


}
