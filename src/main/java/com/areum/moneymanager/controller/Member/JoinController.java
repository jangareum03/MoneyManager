package com.areum.moneymanager.controller.Member;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.service.MailService;
import com.areum.moneymanager.service.member.MemberService;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Slf4j
@Controller
@RequestMapping("/join")
public class JoinController {

    private final MemberService memberService;
    private final MailService mailService;

    @Autowired
    public JoinController(MemberServiceImpl memberService, MailService mailService) {
        this.memberService = memberService;
        this.mailService = mailService;
    }

    @GetMapping
    public String joinView() {
        log.info("[화면요청] 회원가입");
        return "/member/join";
    }

    @PostMapping("/emailCodeCheck")
    @ResponseBody
    public String emailCodeCheck(ReqMemberDto.Join member, String time, HttpServletRequest request ) {
        log.info("[기능요청] 회원가입의 이메일({}) 인증코드 일치 확인 시간({})", member.getEmail(), time);

        HttpSession session = request.getSession();
        return mailService.emailCodeCheck(session, member.getEmail(), member.getCode(), time);
    }

    @PostMapping("/idCheck")
    @ResponseBody
    public int idCheck( ReqMemberDto.Join member ) {
        log.info("[기능요청] 회원가입의 아이디({}) 중복확인",member.getId());
        return memberService.idCheck(member.getId());
    }

    @PostMapping("/nickNameCheck")
    @ResponseBody
    public int nickNameCheck( ReqMemberDto.Join member ) {
        log.info("[기능요청] 회원가입의 닉네임({}) 중복확인", member.getNickName());
        return memberService.nickNameCheck(member.getNickName());
    }

    @PostMapping("/sendEmail")
    @ResponseBody
    public void sendEmailCode(ReqMemberDto.Join member, HttpSession session) throws Exception {
        log.info("[기능요청] 회원가입 이메일({}) 인증코드 전송", member.getEmail());

        String code = mailService.sendMail(member.getEmail());
        session.setAttribute(""+member.getEmail() , code);
    }

}
