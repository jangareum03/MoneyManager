package com.moneymanager.member.controller;

import com.moneymanager.global.domain.dto.response.api.ApiBody;
import com.moneymanager.global.exception.annotation.ApiController;
import com.moneymanager.global.log.operation.annotation.Operation;
import com.moneymanager.global.log.operation.enums.ServiceAction;
import com.moneymanager.member.domain.dto.request.*;
import com.moneymanager.member.domain.dto.response.FindIdResponse;
import com.moneymanager.member.domain.dto.response.FindPwdResponse;
import com.moneymanager.member.service.application.AccountService;
import com.moneymanager.member.service.application.EmailVerificationService;
import com.moneymanager.member.service.application.MemberService;
import com.moneymanager.member.service.application.TokenAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller<br>
 * 파일이름       : AuthApiController<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 6<br>
 * 설명              : 회원 인증 관련 요청을 처리하는 클래스
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
 * 		 	  <td>26. 9. 6</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@RestController
@ApiController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final MemberService memberService;
    private final AccountService accountService;
    private final EmailVerificationService emailVerificationService;
    private final TokenAuthService tokenAuthService;

    @PostMapping("/account/find-id")
    @Operation(ServiceAction.MEMBER_FIND_ID)
    public ApiBody<FindIdResponse> findId(@RequestBody FindIdRequest request) {
        FindIdResponse response = accountService.findId(request);

        return ApiBody.data(response);
    }

    @PostMapping("/account/find-password")
    @Operation(ServiceAction.MEMBER_FIND_PWD)
    public ApiBody<FindPwdResponse> findPwd(@RequestBody FindPwdRequest request) {
        FindPwdResponse response = accountService.findPassword(request);

        return ApiBody.data(response);
    }

    @PostMapping("/email/send-code")
    @Operation(ServiceAction.MEMBER_EMAIL_CODE)
    public ApiBody<Void> sendCode(@RequestBody SendVerificationCodeRequest request){
        //1. 이메일 검증
        emailVerificationService.validateEmail(request.email());

        //2. 이메일 인증코드 발송
        emailVerificationService.sendVerificationCode(request.email());

        //3. 성공 결과 반환
        return ApiBody.message("email.verification.send");
    }

    @PostMapping("/email/verify-code")
    @Operation(ServiceAction.MEMBER_EMAIL_CHECK)
    public ApiBody<String> verifyEmail(@RequestBody EmailVerifyRequest request) {
        String token = emailVerificationService.verifyEmailCode(request.email(), request.code());

        return ApiBody.data(
                "email.verification.success",
                token
        );
    }

    @PostMapping("/refresh")
    @Operation(ServiceAction.MEMBER_TOKEN_REISSUE)
    public ApiBody<Void> verifyRefreshToken(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        tokenAuthService.reissueToken(refreshToken, response);

        return ApiBody.message(null);
    }

    @PostMapping("/signup")
    @Operation(ServiceAction.MEMBER_SIGNUP)
    public ApiBody<Void> signUp(@RequestBody MemberSignUpRequest request) {
        memberService.processSignUp(request);

        return ApiBody.next(
                "member.signup.success",
                "/auth/login"
        );
    }

}