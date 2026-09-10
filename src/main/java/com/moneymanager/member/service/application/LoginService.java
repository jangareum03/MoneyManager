package com.moneymanager.member.service.application;

import com.moneymanager.global.exception.ApplicationException;
import com.moneymanager.global.security.CustomUserDetailService;
import com.moneymanager.global.security.CustomUserDetails;
import com.moneymanager.member.service.validation.AuthValidator;
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
 * 파일이름       : LoginService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 10<br>
 * 설명              : 로그인 흐름을 관리하는 클래스
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
public class LoginService {

    private final CustomUserDetailService userDetailService;
    private final AuthValidator authValidator;

    private final PasswordEncoder passwordEncoder;

    public CustomUserDetails login(String username, String password) {
        try{
            //1. 아이디와 비밀번호 검증
            authValidator.validateLogin(username, password);
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