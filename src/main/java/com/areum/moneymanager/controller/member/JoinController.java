package com.areum.moneymanager.controller.member;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.entity.MemberInfo;
import com.areum.moneymanager.service.member.MailService;
import com.areum.moneymanager.service.member.MemberService;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/members/join")
public class JoinController {

    private final MemberService memberService;
    private final MailService mailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Logger logger = LogManager.getLogger(this.getClass());


    @Autowired
    public JoinController( MemberServiceImpl memberService, MailService mailService, BCryptPasswordEncoder bCryptPasswordEncoder ) {
        this.memberService = memberService;
        this.mailService = mailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping
    public String getJoinView() {
        return "/member/join";
    }

    @ResponseBody
    @PostMapping("/check-email-code")
    public String postEmailCodeCheck(ReqMemberDto.Join member, String time, HttpServletRequest request ) {
        HttpSession session = request.getSession();
        return mailService.emailCodeCheck(session, member.getEmail(), member.getCode(), time);
    }

    @ResponseBody
    @PostMapping("/check-id")
    public int postIdCheck( ReqMemberDto.Join member ) throws Exception {
        return memberService.idCheck(member.getId());
    }

    @ResponseBody
    @PostMapping("/check-nickname")
    public int postNickNameCheck( ReqMemberDto.Join member ) throws Exception {
        return memberService.nickNameCheck(member.getNickName());
    }

    @ResponseBody
    @PostMapping("/send-email")
    public int postSendEmailCode(ReqMemberDto.Join member, HttpSession session) throws Exception {
        String code = mailService.sendMail(member.getEmail(), "email");
        session.setAttribute(""+ member.getEmail() , code);

        return 1;
    }

    @PostMapping
    public String postJoin( ReqMemberDto.Join member ) throws Exception {
        member.encPassword( bCryptPasswordEncoder.encode(member.getPassword()) );
        memberService.joinMember( member );

        return "index";
    }

}
