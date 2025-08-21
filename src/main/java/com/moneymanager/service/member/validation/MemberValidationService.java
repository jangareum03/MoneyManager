package com.moneymanager.service.member.validation;

import com.moneymanager.dao.member.MemberDaoImpl;
import com.moneymanager.dto.member.validation.EmailCheckDTO;
import com.moneymanager.enums.RegexPattern;
import com.moneymanager.exception.code.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import java.util.Objects;

import static com.moneymanager.exception.code.ErrorCode.*;
import static com.moneymanager.enums.RegexPattern.MEMBER_BIRTH;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.service.member.validation<br>
 *  * 파일이름       : MemberValidationService<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원필드의 검증 로직을 처리하는 클래스
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
@Slf4j
@Service
public class MemberValidationService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final MemberDaoImpl memberDAO;

	public MemberValidationService(MemberDaoImpl memberDAO ) {
		this.memberDAO = memberDAO;
	}



	/**
	 * 아이디가 적합한지 확인합니다.
	 *
	 * @param id		아이디
	 * @throws ClientException			아이다가 null이거나 패턴과 일치하지 않을 때
	 */
	public static void checkIdAvailability( String id ) {
		if(Objects.isNull(id) || id.trim().isEmpty() ) {
			throw new ClientException(ErrorCode.MEMBER_ID_NONE, "아이디를 입력해주세요.");
		}else if( !id.trim().matches(RegexPattern.MEMBER_ID.getPattern()) ) {
			throw new ClientException(ErrorCode.MEMBER_ID_FORMAT, "4~15자 사이의 영어와 숫자만 입력 가능합니다.");
		}
	}


	/**
	 * 이미 존재하는 아이디인지 확인합니다.<br>
	 *
	 * @param id			아이디
	 * @throws ClientException 아이디가 존재할 때
	 */
	public void checkIdExistence( String id ) {
		checkIdAvailability(id.trim());

		if(  memberDAO.countByUsername(id) != 0 ) {
			throw new ClientException(ErrorCode.MEMBER_ID_DUPLICATE, "이미 사용중인 아이디입니다.");
		}
	}



	/**
	 * 비밀번호가 적합한지 확인합니다.
	 * @param password			비밀번호
	 * @throws ClientException		비밀번호가 null 이거나 패턴과 일치하지 않을 때
	 */
	public static void checkPasswordAvailability( String password ) {
		password= password.trim();

		if( password.isEmpty() ) {
			throw new ClientException(ErrorCode.MEMBER_PASSWORD_MISSING, "비밀번호를 입력해주세요.");
		}else if( !password.matches(RegexPattern.MEMBER_PWD.getPattern()) ) {
			throw new ClientException(ErrorCode.MEMBER_PASSWORD_FORMAT, "8~20자 사이의 영어,숫자,특수문자(!%#^*)만 입력 가능합니다.");
		}
	}



	/**
	 * 비밀번호가 현재 비밀번호와 동일한지 확인합니다.
	 *
	 * @param 		memberId	회원번호
	 * @param 		password		비밀번호
	 * @throws  	ClientException	현재 비밀번호와 불일치 할 경우
	 */
	public void checkPasswordIdentify( String memberId, String password ) {
		password = password.trim();

		checkPasswordAvailability(password);

		String currentPassword = memberDAO.findPasswordByUsername( memberDAO.findUsernameByMemberId(memberId) );
		if( !passwordEncoder.matches( password, currentPassword ) ) {
			throw new ClientException(ErrorCode.MEMBER_PASSWORD_MISMATCH, "비밀번호가 일치하지 않습니다. 다시 확인해주세요.");
		}
	}



	/**
	 * 이름이 적합한지 확인합니다.
	 *
	 * @param name 	이름
	 * @throws ClientException		이름이 null이거나 패턴과 일치하지 않을 때
	 */
	public static void checkNameAvailability( String name ) {
		name = name.trim();

		if( name.isEmpty() ) {
			throw new ClientException(ErrorCode.MEMBER_NAME_MISSING, "이름을 입력해주세요.");
		}else if( !name.matches(RegexPattern.MEMBER_NAME.getPattern()) ) {
			throw new ClientException(ErrorCode.MEMBER_NAME_FORMAT, "2~5글자 사이의 한글만 입력해야 합니다.");
		}
	}



	public static void checkBirthAvailability( String date ) {
		date = date.trim();

		if( date.isEmpty() ) {
			throw new ClientException(ErrorCode.MEMBER_BIRTH_MISSING, "생년월일을 입력해주세요.");
		}else if( !date.matches(MEMBER_BIRTH.getPattern()) ) {
			throw new ClientException(ErrorCode.MEMBER_BIRTH_FORMAT, "생년월일 형식에 맞게 입력해야 합니다.");
		}
	}



	/**
	 * 닉네임이 적합한지 확인합니다.
	 *
	 * @param nickname 	닉네임
	 * @throws ClientException			닉네임이 null이거나 패턴과 일치하지 않을 때
	 */
	public static void checkNicknameAvailability( String nickname ) {
		nickname = nickname.trim();

		if( nickname.isEmpty() ) {
			throw new ClientException(ErrorCode.MEMBER_NICKNAME_MISSING, "닉네임을 입력해주세요.");
		}else if( !nickname.matches(RegexPattern.MEMBER_NICKNAME.getPattern()) ) {
			throw new ClientException(ErrorCode.MEMBER_NICKNAME_FORMAT, "2 ~ 10자 사이의 한글, 영문자, 숫자만 입력해야 합니다.");
		}
	}



	/**
	 * 이미 존재하는 닉네임인지 확인합니다.
	 *
	 * @param nickname 						닉네임
	 * @throws ClientException 	닉네임이 존재할 때
	 */
	public  void checkNicknameExistence( String nickname ) {
		checkNicknameAvailability(nickname);

		if( memberDAO.countByNickName(nickname) != 0 ) {
			throw new ClientException(ErrorCode.MEMBER_NICKNAME_DUPLICATE, "이미 사용중인 닉네임입니다.");
		}
	}



	/**
	 * 이메일이 적합한지 확인합니다. <br>
	 *
	 * @param email											이메일
	 * @throws ClientException		이메일이 null이거나 패턴과 일치하지 않을 때
	 */
	public static void checkEmailAvailability( String email ) {
		email = email.trim();

		if( email.isEmpty() ) {
			throw new ClientException(ErrorCode.MEMBER_EMAIL_MISSING, "이메일을 입력해주세요.");
		}else if( !email.matches(RegexPattern.MEMBER_EMAIL.getPattern()) ) {
			throw new ClientException(ErrorCode.MEMBER_EMAIL_FORMAT, "이메일 형식에 맞게 입력해야 합니다.");
		}
	}



	/**
	 * 이미 존재하는 이메일인지 확인합니다.
	 *
	 * @param email 										이메일
	 * @throws ClientException 	이메일이 존재할 때
	 */
	public void checkEmailExistence( String email ) {
		checkEmailAvailability(email);

		if( memberDAO.countByEmail(email) != 0 ) {
			throw new ClientException(ErrorCode.MEMBER_EMAIL_DUPLICATE, "이미 사용중인 이메일입니다.");
		}

	}



	/**
	 * 이메일 인증코드가 일치하는지 확인합니다. <br>
	 * 인증코드 입력시간이 초과, 패턴 불일치, 코드값 불일치면 {@link ClientException}이 발생합니다.
	 * 인증코드가 일치하면 session에 저장된 email이 지워집니다.
	 *
	 * @param session						사용자 식별 및 정보를 저장하는 객체
	 * @param emailCheckDTO		이메일 인증 코드 정보를 담은 객체
	 */
	public void checkEmailCode( HttpSession session, EmailCheckDTO emailCheckDTO ) {
		if( Objects.isNull(emailCheckDTO.getEmail()) ) {
			throw new ClientException(ErrorCode.MEMBER_EMAIL_MISSING, "이메일을 입력해주세요.");
		}

		String sentCode = (String)session.getAttribute( emailCheckDTO.getEmail() );

		if( emailCheckDTO.getTimer().getMinute() <= 0 && emailCheckDTO.getTimer().getSecond() <= 0 ) {	//인증시간 초과한 경우
			throw new ClientException(ErrorCode.MEMBER_CODE_TIMEOUT, "시간 초과되었습니다. 다시 전송해주세요.");
		}else {
			if( !emailCheckDTO.getCode().matches(RegexPattern.MEMBER_EMAIL_CODE.getPattern()) ) {
				throw new ClientException(ErrorCode.MEMBER_CODE_FORMAT, "6글자의 영문자, 숫자만 입력할 수 있습니다.");
			}

			if( sentCode.equals(emailCheckDTO.getCode()) ){
				session.removeAttribute(emailCheckDTO.getEmail());
			}else {
				throw new ClientException(ErrorCode.MEMBER_CODE_MISMATCH, "인증코드가 일치하지 않습니다. 다시 입력해주세요.");
			}
		}


	}
}
