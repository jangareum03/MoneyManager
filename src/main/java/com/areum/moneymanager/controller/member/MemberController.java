package com.areum.moneymanager.controller.member;


import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.service.ImageService;
import com.areum.moneymanager.service.member.MemberService;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@Controller
public class MemberController {

    private final MemberService memberService;
    private final ImageService imageService;

    private final Logger LOGGER = LogManager.getLogger(MemberController.class);

    public MemberController(MemberServiceImpl memberService, ImageService imageService) {
        this.memberService = memberService;
        this.imageService = imageService;
    }

    @GetMapping("/deleteMember")
    public ModelAndView getDeleteMemberView(HttpSession session) throws SQLException {
        ModelAndView mav = new ModelAndView();

        String id = memberService.findId((String) session.getAttribute("mid"));
        mav.addObject("id", id);
        mav.setViewName("/include/popup_delete");

        return mav;
    }

    @GetMapping("/login-success")
    public String getLogin( @AuthenticationPrincipal ResMemberDto.AuthMember authMember, Model model, HttpSession session ) throws SQLException {
        String id = authMember.getId();
        String pwd = authMember.getPassword();

        int result = memberService.loginCheck(id, pwd);
        switch ( result ) {
            case -2 :
                LOGGER.info("[복구 가능한 탈퇴회원 로그인] 아이디: {}, 비밀번호: {}", id, pwd);

                model.addAttribute("msg", "탈퇴한지 30일이 지나지 않아 계정을 복구합니다.");
                model.addAttribute("method", "post");
                model.addAttribute("url", "/recoverMember");
                model.addAttribute("id", id);
                model.addAttribute("pwd", pwd);

                return "alert";
            case -3:
                model.addAttribute("msg", "탈퇴한지 30일이 경과하여 계정을 복구할 수 없습니다. 다시 가입해주시길 바랍니다..");
                model.addAttribute("method", "get");
                model.addAttribute("url", "/");

                return "alert";
            default:
                String mid = memberService.findMid( id, pwd );
                session.setAttribute("mid", mid);

                //프로필 사진
                ResMemberDto.Member member = memberService.findMember( mid );
                session.setAttribute("profile", member.getProfile() == null ? null : imageService.findProfile( mid, member.getProfile(), memberService.findUpdateHistory( mid, 'I' ) ));
                session.setAttribute("nickName", member.getNickName());

                return "redirect:/home";
        }
    }

    @GetMapping
    public String getLoginView( @RequestParam(value = "error", required = false)String error,
                                @RequestParam(value = "exception", required = false)String exception, Model model ) {
            model.addAttribute("error", error);
            model.addAttribute("exception", exception);

        return "index";
    }

    @GetMapping("/logout")
    public String getLogout( ReqMemberDto.Login member, HttpSession session) {
        String mid = (String) session.getAttribute("mid");

        if (mid != null) {
            session.removeAttribute("mid");
        }

        return "index";
    }


    @GetMapping("/changePwd")
    public String getPwdChangeView() {
        return "/include/popup_pwdChange";
    }

    @ResponseBody
    @PostMapping("/findPwd")
    public int postFindPassword( String password, HttpSession session ) throws Exception {
        String mid = (String) session.getAttribute("mid");

        return memberService.findPwd( mid, password);
    }

    @ResponseBody
    @PostMapping("/updatePwd")
    public int postUpdatePassword( ReqMemberDto.Update update, HttpSession session ) {
        String mid = (String)session.getAttribute("mid");

        try{
            memberService.modifyMember( mid, update );
            return 1;
        }catch( Exception e ){
            return 0;
        }
    }

    @ResponseBody
    @PostMapping("/deleteMember")
    public int postDeleteMember( ReqMemberDto.Delete delete, HttpSession session ) {
        String mid = (String)session.getAttribute("mid");

        try{
            memberService.deleteMember( mid, delete );
            return 1;
        }catch( Exception e ){
            e.printStackTrace();
            return 0;
        }
    }

    @PostMapping("/recoverMember")
    public String postRecoverMember( ReqMemberDto.Login login, HttpSession session ) throws SQLException {
        LOGGER.info("[탈퇴회원 복구] 아이디: {}, 비밀번호: {}", login.getId(), login.getPassword());

        String mid = memberService.findMid( login.getId(), login.getPassword() );
        memberService.recoverMember( mid, login );

        return "forward:/login";
    }
}
