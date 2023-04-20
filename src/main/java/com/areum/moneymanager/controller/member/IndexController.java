package com.areum.moneymanager.controller.member;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    //로그인화면 요청
    @GetMapping
    public String getLoginView()  {
        return "index";
    }

}
