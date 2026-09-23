package com.moneymanager.member.controller.api;

import com.moneymanager.global.domain.dto.response.api.ApiBody;
import com.moneymanager.global.exception.annotation.ApiController;
import com.moneymanager.global.log.operation.annotation.Operation;
import com.moneymanager.global.log.operation.enums.ServiceAction;
import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.member.domain.dto.request.EmailUpdateRequest;
import com.moneymanager.member.domain.dto.request.EmailVerifyRequest;
import com.moneymanager.member.domain.dto.request.MemberUpdateRequest;
import com.moneymanager.member.domain.dto.request.MemberWithdrawalRequest;
import com.moneymanager.member.domain.dto.response.MemberUpdateResponse;
import com.moneymanager.member.service.application.AccountService;
import com.moneymanager.member.service.application.EmailVerificationService;
import com.moneymanager.member.service.application.MemberUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller<br>
 * 파일이름       : MemberApiController<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 16<br>
 * 설명              : 회원 기능 관련 요청을 처리하는 API 컨트롤러
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
 * 		 	  <td>26. 9. 16</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ApiController
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {

    private final AccountService accountService;
    private final MemberUpdateService updateService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/email/verify-code")
    @Operation(ServiceAction.MEMBER_EMAIL_CHECK)
    public ApiBody<Void> verifyEmail(@RequestBody EmailVerifyRequest request) {
        emailVerificationService.verifyCode(request.email(), request.code());

        return ApiBody.<Void>builder()
                .messageKey("email.verification.success")
                .build();
    }

    @PutMapping("/email")
    @Operation(ServiceAction.MEMBER_UPDATE)
    public ApiBody<MemberUpdateResponse> updateEmail(@AuthenticationPrincipal CustomUserDetails currentUser, @RequestBody EmailUpdateRequest request) {
        MemberUpdateResponse response = updateService.update(currentUser.getId(), request);

        return ApiBody.<MemberUpdateResponse>builder()
                .data(response)
                .build();
    }

    @PatchMapping("/me")
    @Operation(ServiceAction.MEMBER_UPDATE)
    public ApiBody<MemberUpdateResponse> updateMember(@AuthenticationPrincipal CustomUserDetails currentUser, @RequestBody MemberUpdateRequest request) {
        MemberUpdateResponse response = updateService.update(currentUser.getId(), request);

        return ApiBody.<MemberUpdateResponse>builder()
                .data(response)
                .build();
    }

    @PutMapping("/profile")
    @Operation(ServiceAction.MEMBER_UPDATE)
    public ApiBody<MemberUpdateResponse> updateProfile(@AuthenticationPrincipal CustomUserDetails currentUser, @RequestBody MultipartFile file) {
        MemberUpdateResponse response = updateService.update(currentUser.getId(), file);

        return ApiBody.<MemberUpdateResponse>builder()
                .data(response)
                .build();
    }

    @DeleteMapping("/withdrawal")
    @Operation(ServiceAction.MEMBER_WITHDRAWAL)
    public ApiBody<Void> withdrawal(@AuthenticationPrincipal CustomUserDetails currentUser, @RequestBody MemberWithdrawalRequest request) {
        accountService.withdrawal(currentUser.getId(), request);

        return ApiBody.<Void>builder()
                .messageKey("member.withdrawal.success")
                .next("/login")
                .build();
    }

}