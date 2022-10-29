package com.areum.moneymanager.controller.member;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.service.member.MemberService;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {

    private MemberService memberService;
    private final Logger LOGGER = LogManager.getLogger(MemberController.class);

    @Autowired
    public MemberController(MemberServiceImpl memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/")
    public String getLoginView( @ModelAttribute("member") ReqMemberDto.Login member ){
        return "index";
    }

    @PostMapping("/login")
    public String postLogin( @ModelAttribute("member") ReqMemberDto.Login member, BindingResult bindingResult ) {
        if( !StringUtils.hasText(member.getId()) ) {
            bindingResult.rejectValue("id", "noInput");
        }else if( !StringUtils.hasText(member.getPwd()) ) {
            bindingResult.rejectValue("pwd", "noInput");
        }else{
            int result = memberService.loginCheck( member );
            if( result == 0 ) {
                bindingResult.rejectValue("id", "noMember");
            }else if( result == -1 ) {
                bindingResult.rejectValue("id", "mismatch");
            }
        }

        if( bindingResult.hasErrors() ) {
            return "index";
        }else{
            return "/service/home";
        }

    }
}
