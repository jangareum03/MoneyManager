package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.member.domain.dto.request.MemberSignUpRequest;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.repository.MemberRepository;
import com.moneymanager.member.service.command.MemberAppender;
import com.moneymanager.member.service.validation.MemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import static com.moneymanager.global.exception.code.ErrorCode.DATA_PERSISTENCE_FAILED;
import static com.moneymanager.global.exception.code.ErrorCode.DUPLICATE_DATA;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : SignupService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 19<br>
 * 설명              : 회원가입 흐름을 관리하는 오케스트라 클래스
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
 * 		 	  <td>26. 9. 19</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class SignupService {

    private final EmailVerificationService emailVerificationService;
    private final MemberRepository memberRepository;

    private final MemberAppender memberAppender;
    private final MemberValidator validator;

    public void validateDuplicationUsername(String username) {
        //아이디 입력값 검증
        validator.validateUsername(username);

        if(memberRepository.existsByUsername(username)){
            throw new ApplicationException(
                    DUPLICATE_DATA,
                    LogContent.of(
                            "중복회원 검증",
                            "username",
                            StringUtil.maskMiddle(username)
                    ).withCause("중복 아이디")
            ).withMessageKey("member.username.duplicate");
        }
    }

    public String confirmEmail(String email, String code) {
        emailVerificationService.verifyCode(email, code);

        //인증코드 발급 및 저장
        return emailVerificationService.issueEmailToken(email);
    }

    public void signup(MemberSignUpRequest request) {
        //이메일 인증토큰 검증
        emailVerificationService.verifyAuthToken(request.getEmail(), request.getToken());

        //입력값 검증
        validator.validateSignup(request);

        //회원 생성
        Member member = memberAppender.create(request);

        //회원 저장
        try{
            memberAppender.save(member);
        }catch (DataAccessException e) {
            //인증토큰 삭제
            emailVerificationService.deleteToken(request.getEmail());

            throw new ApplicationException(
                    DATA_PERSISTENCE_FAILED,
                    LogContent.of(
                            "Member 저장",
                            Member.class
                    ).withCause("회원저장 실패"),
                    e
            ).withMessageKey("member.signup.unavailable");
        }

        //인증토큰 삭제
        emailVerificationService.deleteToken(request.getEmail());
    }

}