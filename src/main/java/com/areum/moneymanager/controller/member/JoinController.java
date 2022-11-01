package com.areum.moneymanager.controller.member;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.service.MailService;
import com.areum.moneymanager.service.member.MemberService;
import com.areum.moneymanager.service.member.MemberServiceImpl;
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
    private final Logger LOGGER = LogManager.getLogger(JoinController.class);


    @Autowired
    public JoinController(MemberServiceImpl memberService, MailService mailService) {
        this.memberService = memberService;
        this.mailService = mailService;
    }

    @GetMapping
    public String getJoinView() {
        return "/member/join";
    }

    @ResponseBody
    @PostMapping("/emailCodeCheck")
    public String postEmailCodeCheck(ReqMemberDto.Join member, String time, HttpServletRequest request ) {
        LOGGER.info("이메일 인증코드 확인");
        HttpSession session = request.getSession();

        return mailService.emailCodeCheck(session, member.getEmail(), member.getCode(), time);
    }

    @ResponseBody
    @PostMapping("/idCheck")
    public int postIdCheck( ReqMemberDto.Join member ) {
        LOGGER.info("아이디 중복 확인");
        return memberService.idCheck(member.getId());
    }

    @ResponseBody
    @PostMapping("/nickNameCheck")
    public int postNickNameCheck( ReqMemberDto.Join member ) {
        LOGGER.info("닉네임 중복 확인");
        return memberService.nickNameCheck(member.getNickName());
    }

    @ResponseBody
    @PostMapping("/sendEmail")
    public void postSendEmailCode(ReqMemberDto.Join member, HttpSession session) throws Exception {
        String code = mailService.sendMail(member.getEmail());
        session.setAttribute(""+member.getEmail() , code);

        LOGGER.info("이메일 전송 완료");
    }

    @PostMapping
    public String postJoin( @ModelAttribute("member") ReqMemberDto.Join member ) throws Exception {
        String mid = memberService.makeMemberId(member.getId());

        memberService.joinMember( member, mid );
        LOGGER.info("회원가입 완료");

        return "index";
    }

}
