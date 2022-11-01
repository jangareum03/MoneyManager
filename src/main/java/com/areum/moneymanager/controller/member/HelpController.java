package com.areum.moneymanager.controller.member;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.service.member.MemberService;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

@Controller
public class HelpController {

    private MemberService memberService;

    public HelpController(MemberServiceImpl memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/helpId")
    public String getHelpIdView() {
        return "/member/help_id";
    }

    @GetMapping("/helpPwd")
    public String getHelpPwdView() {
        return "/member/help_pwd";
    }

    @PostMapping("/helpId/find")
    public ModelAndView postFindIdView( ReqMemberDto.FindId findId ) {
        ModelAndView mav = new ModelAndView();

        ResMemberDto.FindId member = memberService.findId(findId);
        if(member == null) {
            mav.setViewName("/member/find_id_error");
        }else{
            mav.addObject("id", member.getId());
            mav.addObject("date", new SimpleDateFormat("yyyy년 MM월 dd일 E요일 hh시 mm분 ss초").format(member.getDate()));
            mav.setViewName("/member/find_id");
        }

        return mav;
    }



}
