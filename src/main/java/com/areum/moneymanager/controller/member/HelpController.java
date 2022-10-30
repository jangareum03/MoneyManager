package com.areum.moneymanager.controller.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelpController {

    @GetMapping("/helpId")
    public String getHelpIdView() {
        return "/member/help_id";
    }

    @GetMapping("/helpPwd")
    public String getHelpPwdView() {
        return "/member/help_pwd";
    }

}
