package com.moneymanager.member.controller.api;

import com.moneymanager.global.domain.dto.response.api.ApiBody;
import com.moneymanager.global.exception.annotation.ApiController;
import com.moneymanager.global.log.operation.annotation.Operation;
import com.moneymanager.global.log.operation.enums.ServiceAction;
import com.moneymanager.member.domain.dto.request.FindIdRequest;
import com.moneymanager.member.domain.dto.request.FindPwdRequest;
import com.moneymanager.member.domain.dto.request.MemberSignUpRequest;
import com.moneymanager.member.domain.dto.response.FindIdResponse;
import com.moneymanager.member.domain.dto.response.FindPwdResponse;
import com.moneymanager.member.service.application.AccountService;
import com.moneymanager.member.service.application.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller.api<br>
 * 파일이름       : AccountApiController<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 22<br>
 * 설명              : 계정 요청을 처리하는 API 컨트롤러
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
 * 		 	  <td>26. 9. 22</td>
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
public class AccountApiController {

    private final SignupService signupService;
    private final AccountService accountService;

    @PostMapping("/signup")
    @Operation(ServiceAction.MEMBER_SIGNUP)
    public ApiBody<Void> signUp(@RequestBody MemberSignUpRequest request) {
        signupService.signup(request);

        return ApiBody.<Void>builder()
                .messageKey("member.signup.success")
                .next("/auth/login")
                .build();
    }

    @PostMapping("/account/find/id")
    @Operation(ServiceAction.MEMBER_FIND_ID)
    public ApiBody<FindIdResponse> findId(@RequestBody FindIdRequest request) {
        FindIdResponse response = accountService.findId(request);

        return ApiBody.<FindIdResponse>builder()
                .data(response)
                .build();
    }

    @PostMapping("/account/find/password")
    @Operation(ServiceAction.MEMBER_FIND_PWD)
    public ApiBody<FindPwdResponse> findPwd(@RequestBody FindPwdRequest request) {
        FindPwdResponse response = accountService.findPassword(request);

        return ApiBody.<FindPwdResponse>builder()
                .data(response)
                .build();
    }

}