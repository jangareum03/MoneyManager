package com.areum.moneymanager.controller.member;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.service.MailService;
import com.areum.moneymanager.service.member.MemberService;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

@Controller
public class HelpController {

    private MemberService memberService;
    private MailService mailService;

    @Autowired
    public HelpController( MemberServiceImpl memberService, MailService mailService ) {
        this.memberService = memberService;
        this.mailService = mailService;
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
            StringBuffer sb = new StringBuffer(member.getId());
            mav.addObject("id", sb.replace(member.getId().length()-3, member.getId().length(), "***"));
            mav.addObject("date", new SimpleDateFormat("yyyy년 MM월 dd일 E요일 hh시 mm분 ss초").format(member.getDate()));
            mav.setViewName("/member/find_id");
        }

        return mav;
    }

    @PostMapping("/helpPwd/find")
    public ModelAndView postFindPwdView( ReqMemberDto.FindPwd findPwd ) throws Exception {
        ModelAndView mav = new ModelAndView();

        ResMemberDto.FindPwd member = memberService.findPwd(findPwd);
        if( member == null ) {
            mav.setViewName("/member/find_pwd_error");
        }else{
            //임시 비밀번호 전송
            String pwd = mailService.sendMail(member.getEmail(), "password");
            memberService.changePwd( findPwd, pwd );

            StringBuffer sb = new StringBuffer(member.getEmail());
            int index = member.getEmail().indexOf("@");
            mav.addObject("email", sb.replace(index -3, index, "***"));
            mav.setViewName("/member/find_pwd");
        }

        return mav;
    }



}
