package com.moneymanager.member.service.application;

import com.moneymanager.member.domain.dto.request.MemberSignUpRequest;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.member.service.command.MemberCommandService;
import com.moneymanager.member.service.read.MemberReadService;
import com.moneymanager.member.service.validation.MemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : MemberService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 5<br>
 * 설명              : 회원 기능 로직 흐름을 관리하는 클래스
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
 * 		 	  <td>26. 9. 5</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberCommandService memberCommandService;
    private final MemberReadService memberReadService;
    private final EmailVerificationService emailVerificationService;

    private final MemberValidator memberValidator;

    public void processSignUp(MemberSignUpRequest request) {
        //요청 객체 검증
        memberValidator.signUp(request);

        //이메일 검증 완료했는지 확인
        emailVerificationService.verifyEmail(request.getEmail(), request.getToken());

        //회원가입 중복 검증
        memberReadService.validateSignUpEligibility(request.getUsername(), request.getNickname());

        //요청객체 변환
        Member member = memberCommandService.create(request);

        //회원 저장
        memberCommandService.save(member);

        //인증토큰 삭제
        emailVerificationService.deleteTokenByEmail(member.getEmail());
    }

}