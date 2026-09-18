package com.moneymanager.member.controller;

import com.moneymanager.global.domain.dto.response.api.ApiBody;
import com.moneymanager.global.exception.annotation.ApiController;
import com.moneymanager.global.log.operation.annotation.Operation;
import com.moneymanager.global.log.operation.enums.ServiceAction;
import com.moneymanager.member.domain.dto.request.EmailUpdateRequest;
import com.moneymanager.member.domain.dto.request.MemberUpdateRequest;
import com.moneymanager.member.domain.dto.response.MemberUpdateResponse;
import com.moneymanager.member.service.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller<br>
 * 파일이름       : MemberApiController<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 16<br>
 * 설명              : 회원 기능 관련 요청을 처리하는 클래스
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

    private final MemberService memberService;

    @PutMapping("/email")
    @Operation(ServiceAction.MEMBER_UPDATE)
    public ApiBody<MemberUpdateResponse> updateEmail(@RequestBody EmailUpdateRequest request) {
        MemberUpdateResponse response = memberService.changeEmail(request);

        return ApiBody.data(response);
    }

    @PatchMapping("/me")
    @Operation(ServiceAction.MEMBER_UPDATE)
    public ApiBody<MemberUpdateResponse> updateMember(@RequestBody MemberUpdateRequest request) {
        MemberUpdateResponse response = memberService.processMemberUpdate(request);

        return ApiBody.data(response);
    }

    @PutMapping("/profile")
    @Operation(ServiceAction.MEMBER_UPDATE)
    public ApiBody<MemberUpdateResponse> updateProfile(@RequestBody MultipartFile file) {
        MemberUpdateResponse response = memberService.changeProfile(file);

        return ApiBody.data(response);
    }

}