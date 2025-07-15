package com.areum.moneymanager.service.member.validation;

import com.areum.moneymanager.dao.member.MemberInfoDao;
import com.areum.moneymanager.dao.member.MemberInfoDaoImpl;
import com.areum.moneymanager.dto.request.member.validation.MemberRequestDTO;
import com.areum.moneymanager.enums.RegexPattern;
import com.areum.moneymanager.exception.ErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import java.util.Objects;

import static com.areum.moneymanager.enums.ErrorCode.*;
import static com.areum.moneymanager.enums.RegexPattern.MEMBER_BIRTH;


/**
 * 회원 데이터가 적합하게 입력되었는지 검증하는 클래스</br>
 *
 * @version 1.0
 */
@Service
public class MemberValidationService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final MemberInfoDao memberDAO;
	private final Logger logger = LogManager.getLogger(this);

	public MemberValidationService(MemberInfoDaoImpl memberDAO ) {
		this.memberDAO = memberDAO;
	}



	/**
	 * 아이디가 적합한지 확인합니다.
	 *
	 * @param id		아이디
	 * @throws ErrorException			아이다가 null이거나 패턴과 일치하지 않을 때
	 */
	public static void checkIdAvailability( String id ) {
		if(Objects.isNull(id) || id.trim().isEmpty() ) {
			throw new ErrorException(MEMBER_ID_NONE);
		}else if( !id.trim().matches(RegexPattern.MEMBER_ID.getPattern()) ) {
			throw new ErrorException(MEMBER_ID_FORMAT);
		}
	}


	/**
	 * 이미 존재하는 아이디인지 확인합니다.<br>
	 *
	 * @param id			아이디
	 * @throws ErrorException 아이디가 존재할 때
	 */
	public void checkIdExistence( String id ) throws ErrorException {
		checkIdAvailability(id.trim());

		if(  memberDAO.countByUsernameEquals(id) != 0 ) {
			throw new ErrorException(MEMBER_ID_EXITS);
		}
	}



	/**
	 * 비밀번호가 적합한지 확인합니다.
	 * @param password			비밀번호
	 * @throws ErrorException		비밀번호가 null 이거나 패턴과 일치하지 않을 때
	 */
	public static void checkPasswordAvailability( String password ) {
		password= password.trim();

		if( password.isEmpty() ) {
			throw new ErrorException(MEMBER_PASSWORD_NONE);
		}else if( !password.matches(RegexPattern.MEMBER_PWD.getPattern()) ) {
			throw new ErrorException(MEMBER_PASSWORD_FORMAT);
		}
	}



	/**
	 * 비밀번호가 현재 비밀번호와 동일한지 확인합니다.
	 *
	 * @param 		memberId	회원번호
	 * @param 		password		비밀번호
	 * @throws  	ErrorException	현재 비밀번호와 불일치 할 경우
	 */
	public void checkPasswordIdentify( String memberId, String password ) {
		password = password.trim();

		checkPasswordAvailability(password);

		String currentPassword = memberDAO.findPasswordByUsername( memberDAO.findUsernameByMemberId(memberId) );
		if( !passwordEncoder.matches( password, currentPassword ) ) {
			throw new ErrorException(MEMBER_PASSWORD_MISMATCH);
		}
	}



	/**
	 * 이름이 적합한지 확인합니다.
	 *
	 * @param name 	이름
	 * @throws ErrorException		이름이 null이거나 패턴과 일치하지 않을 때
	 */
	public static void checkNameAvailability( String name ) {
		name = name.trim();

		if( name.isEmpty() ) {
			throw new ErrorException(MEMBER_NAME_NONE);
		}else if( !name.matches(RegexPattern.MEMBER_NAME.getPattern()) ) {
			throw new ErrorException(MEMBER_NAME_FORMAT);
		}
	}



	public static void checkBirthAvailability( String date ) {
		date = date.trim();

		if( date.isEmpty() ) {
			throw new ErrorException(MEMBER_BIRTH_NONE);
		}else if( !date.matches(MEMBER_BIRTH.getPattern()) ) {
			throw new ErrorException(MEMBER_BIRTH_FORMAT);
		}
	}



	/**
	 * 닉네임이 적합한지 확인합니다.
	 *
	 * @param nickname 	닉네임
	 * @throws ErrorException			닉네임이 null이거나 패턴과 일치하지 않을 때
	 */
	public static void checkNicknameAvailability( String nickname ) {
		nickname = nickname.trim();

		if( nickname.isEmpty() ) {
			throw new ErrorException(MEMBER_NICKNAME_NONE);
		}else if( !nickname.matches(RegexPattern.MEMBER_NICKNAME.getPattern()) ) {
			throw new ErrorException(MEMBER_NICKNAME_FORMAT);
		}
	}



	/**
	 * 이미 존재하는 닉네임인지 확인합니다.
	 *
	 * @param nickname 						닉네임
	 * @throws ErrorException 	닉네임이 존재할 때
	 */
	public  void checkNicknameExistence( String nickname ) throws ErrorException {
		checkNicknameAvailability(nickname);

		if( memberDAO.countByNickNameEquals(nickname) != 0 ) {
			throw new ErrorException(MEMBER_NICKNAME_EXITS);
		}
	}



	/**
	 * 이메일이 적합한지 확인합니다. <br>
	 *
	 * @param email											이메일
	 * @throws ErrorException		이메일이 null이거나 패턴과 일치하지 않을 때
	 */
	public static void checkEmailAvailability( String email ) {
		email = email.trim();

		if( email.isEmpty() ) {
			throw new ErrorException(MEMBER_EMAIL_NONE);
		}else if( !email.matches(RegexPattern.MEMBER_EMAIL.getPattern()) ) {
			throw new ErrorException(MEMBER_EMAIL_FORMAT);
		}
	}



	/**
	 * 이미 존재하는 이메일인지 확인합니다.
	 *
	 * @param email 										이메일
	 * @throws ErrorException 	이메일이 존재할 때
	 */
	public void checkEmailExistence( String email ) throws ErrorException {
		checkEmailAvailability(email);

		if( memberDAO.countByEmailEquals(email) != 0 ) {
			throw new ErrorException(MEMBER_EMAIL_EXITS);
		}

	}



	/**
	 * 이메일 인증코드가 일치하는지 확인합니다. <br>
	 * 인증코드 입력시간이 초과, 패턴 불일치, 코드값 불일치면 {@link ErrorException}이 발생합니다.
	 * 인증코드가 일치하면 session에 저장된 email이 지워집니다.
	 *
	 * @param session	사용자 식별 및 정보를 저장하는 객체
	 * @param inputCode		이메일 인증 코드
	 */
	public void checkEmailCode( HttpSession session, MemberRequestDTO.EmailCodeCheck inputCode ) {
		if( Objects.isNull(inputCode.getEmail()) ) {
			throw new ErrorException(MEMBER_EMAIL_NONE);
		}

		String sentCode = (String)session.getAttribute( inputCode.getEmail() );

		if( inputCode.getTime().getMinute() <= 0 && inputCode.getTime().getSecond() <= 0 ) {	//인증시간 초과한 경우
			throw new ErrorException(MEMBER_JOIN_TIMEOUT);
		}else {
			if( !inputCode.getCode().matches(RegexPattern.MEMBER_EMAIL_CODE.getPattern()) ) {
				throw new ErrorException(MEMBER_CODE_FORMAT);
			}

			if( sentCode.equals(inputCode.getCode()) ){
				session.removeAttribute(inputCode.getEmail());
			}else {
				throw new ErrorException(MEMBER_CODE_MISMATCH);
			}
		}


	}
}
