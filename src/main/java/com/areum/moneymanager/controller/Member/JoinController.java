package com.areum.moneymanager.controller.Member;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.service.MailService;
import com.areum.moneymanager.service.member.MemberService;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/join")
public class JoinController {

    private final MemberService memberService;
    private final MailService mailService;
    private static final Logger LOGGER = LogManager.getLogger(JoinController.class);


    @Autowired
    public JoinController(MemberServiceImpl memberService, MailService mailService) {
        this.memberService = memberService;
        this.mailService = mailService;
    }

    @GetMapping
    public String getJoinView() {
        LOGGER.info("회원가입 화면 요청");
        return "/member/join";
    }

    @ResponseBody
    @PostMapping("/emailCodeCheck")
    public String postEmailCodeCheck(ReqMemberDto.Join member, String time, HttpServletRequest request ) {
        HttpSession session = request.getSession();
        return mailService.emailCodeCheck(session, member.getEmail(), member.getCode(), time);
    }

    @ResponseBody
    @PostMapping("/idCheck")
    public int postIdCheck( ReqMemberDto.Join member ) {
        return memberService.idCheck(member.getId());
    }

    @ResponseBody
    @PostMapping("/nickNameCheck")
    public int postNickNameCheck( ReqMemberDto.Join member ) {
        return memberService.nickNameCheck(member.getNickName());
    }

    @ResponseBody
    @PostMapping("/sendEmail")
    public void postSendEmailCode(ReqMemberDto.Join member, HttpSession session) throws Exception {

        String code = mailService.sendMail(member.getEmail());
        session.setAttribute(""+member.getEmail() , code);
    }

    @PostMapping
    public void postJoin() {
    }

}
