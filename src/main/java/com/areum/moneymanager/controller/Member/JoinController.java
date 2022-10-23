package com.areum.moneymanager.controller.Member;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.service.MemberService;
import com.areum.moneymanager.service.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping("/join")
public class JoinController {

    private final MemberService memberService;

    @Autowired
    public JoinController(MemberServiceImpl memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/idCheck")
    @ResponseBody
    public int idCheck( ReqMemberDto.Join member ) {
        log.info("[기능요청] 회원가입의 아이디({}) 중복확인",member.getId());
        return memberService.idCheck(member.getId());
    }

    @GetMapping
    public String joinView() {
        log.info("[화면요청] 회원가입");
        return "/member/join";
    }

    @PostMapping("/nickNameCheck")
    @ResponseBody
    public int nickNameCheck( ReqMemberDto.Join member ) {
        log.info("[기능요청] 회원가입의 닉네임({}) 중복확인", member.getNickName());
        return memberService.nickNameCheck(member.getNickName());
    }
}
