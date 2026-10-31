package com.moneymanager.member.controller.view;

import com.moneymanager.global.annotation.ViewController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.controller.view<br>
 * 파일이름       : AccountViewController<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 2026-11-01<br>
 * 역할             : 계정 화면 요청을 처리하는 컨트롤러
 * </p>
 */
@ViewController
public class AccountViewController {

    @GetMapping("/signup")
    public ModelAndView login() {
        ModelAndView mav = new ModelAndView();

        mav.addObject("bodyClass", "signup-page");
        mav.setViewName("/auth/signup");

        return mav;
    }

}