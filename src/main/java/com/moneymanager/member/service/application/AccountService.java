package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.security.CustomUserDetailService;
import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.global.util.string.StringUtil;
import com.moneymanager.member.domain.dto.request.FindIdRequest;
import com.moneymanager.member.domain.dto.response.FindIdResponse;
import com.moneymanager.member.domain.query.MemberFindIdQuery;
import com.moneymanager.member.service.read.MemberReadService;
import com.moneymanager.member.service.validation.AccountValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.service.application<br>
 * 파일이름       : AccountService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 10<br>
 * 설명              : 계정 로직을 관리하는 오케스트라 클래스
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
 * 		 	  <td>26. 9. 10</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final CustomUserDetailService userDetailService;
    private final AccountValidator accountValidator;
    private final PasswordEncoder passwordEncoder;

    private final MemberReadService memberReadService;

    public CustomUserDetails login(String username, String password) {
        try{
            //1. 아이디와 비밀번호 검증
            accountValidator.validateLogin(username, password);
        }catch (ApplicationException e){
            throw new AuthenticationServiceException(e.getUserMessage());
        }

        //2. 사용자 정보 조회
        CustomUserDetails userDetails = (CustomUserDetails) userDetailService.loadUserByUsername(username);
        throwsAuthenticationException(userDetails);

        //3. 비밀번호 일치여부 검증
        if(!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("member.login.failed");
        }

        return userDetails;
    }

    public FindIdResponse findId(FindIdRequest request) {
        //1. 이름과 이메일 입력 검증
        accountValidator.validateFindId(request);

        //2. 아이디 + 회원상태 조회
        MemberFindIdQuery memberFindIdQuery = memberReadService.getMemberStatus(request.getName(), request.getEmail());

        //3. 마스킹 처리
        String id = memberFindIdQuery.getUsername();
        String maskingId = StringUtil.masking(id, 1, id.length() / 2);

        return new FindIdResponse(maskingId, memberFindIdQuery.getStatus());
    }


    //===== login 보조 메서드 =====
    private void throwsAuthenticationException(CustomUserDetails userDetails) throws AuthenticationException {
        if(!userDetails.isAccountNonExpired()) {
            throw new DisabledException("member.login.not_found");
        }

        if(!userDetails.isAccountNonLocked()) {
            throw new LockedException("member.login.locked");
        }

        if(!userDetails.isEnabled()) {
            throw new DisabledException("member.login.restricted");
        }
    }

}