package com.areum.moneymanager.controller.member;


import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.service.member.MemberService;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class MemberController {

    private final MemberService memberService;

    public MemberController( MemberServiceImpl memberService ) {
        this.memberService = memberService;
    }

    @GetMapping
    public String getLoginView( @ModelAttribute("member") ReqMemberDto.Login member ){
        return "index";
    }

    @PostMapping("/login")
    public String postLogin(@ModelAttribute("member") ReqMemberDto.Login member, BindingResult bindingResult, HttpSession session) throws Exception {
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
            String mid = memberService.findMid(member);
            session.setAttribute("mid", mid);

            return "redirect:/home";
        }
    }

}
