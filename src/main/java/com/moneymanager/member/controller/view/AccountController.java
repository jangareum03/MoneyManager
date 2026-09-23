package com.moneymanager.member.controller.view;

import com.moneymanager.global.log.operation.annotation.Operation;
import com.moneymanager.global.log.operation.enums.ServiceAction;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller.view<br>
 * 파일이름       : AccountController<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 22<br>
 * 설명              : 계정 화면 요청을 처리하는 컨트롤러
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
public class AccountController {

    @GetMapping("/login")
    @Operation(ServiceAction.LOGIN)
    public String login(@SessionAttribute(value = "loginError", required = false)String error, Model model) {
        model.addAttribute("loginError", error);

        return "/member/member_login";
    }

    @GetMapping("/signup")
    @Operation(ServiceAction.SIGNUP)
    public String showSignupForm() {
        return "/member/signup";
    }

    @GetMapping("/account/find-id")
    @Operation(ServiceAction.MEMBER_FIND_ID_VIEW)
    public String findId() {
        return "/member/recovery_id";
    }

}
