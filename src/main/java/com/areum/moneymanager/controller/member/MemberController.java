package com.areum.moneymanager.controller.member;


import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.service.ImageService;
import com.areum.moneymanager.service.LogService;
import com.areum.moneymanager.service.member.MemberService;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@Controller
public class MemberController {

    private final MemberService memberService;
    private final ImageService imageService;
    private final LogService logService;
    private final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    public MemberController(MemberServiceImpl memberService, ImageService imageService, LogService logService) {
        this.memberService = memberService;
        this.imageService = imageService;
        this.logService = logService;
    }

    @GetMapping("/deleteMember")
    public ModelAndView getDeleteMemberView( HttpSession session ) throws SQLException {
        String id = memberService.findId((String) session.getAttribute("mid"));

        ModelAndView mav = new ModelAndView();
        mav.addObject("id", id);
        mav.setViewName("/include/popup_delete");

        logger.debug("{} 아이디로 탈퇴 화면 요청", id);
        return mav;
    }

    @GetMapping("/login-success")
    public String getLogin(@AuthenticationPrincipal ReqMemberDto.AuthMember authMember, Model model, HttpSession session, HttpServletRequest request ) throws SQLException {
        String url, cause = null;
        char success;

        String id = authMember.getId();
        String pwd = authMember.getPassword();

        String mid = memberService.findMid( id, pwd );

        int result = memberService.loginCheck( id, pwd );
        switch ( result ) {
            case -2 :
                logger.debug("복구 가능한 아이디({})와 비밀번호({})로 로그인 요청", authMember.getId(), authMember.getPassword());

                model.addAttribute("msg", "탈퇴한지 30일이 지나지 않아 계정을 복구합니다.");
                model.addAttribute("method", "post");
                model.addAttribute("url", "/recoverMember");
                model.addAttribute("id", id);
                model.addAttribute("pwd", pwd);

                success = 'y';
                url = "alert";
            case -3:
                logger.debug("복구 불가능한 아이디({})와 비밀번호({})로 로그인 요청", authMember.getId(), authMember.getPassword());

                model.addAttribute("msg", "탈퇴한지 30일이 경과하여 계정을 복구할 수 없습니다. 다시 가입해주시길 바랍니다..");
                model.addAttribute("method", "get");
                model.addAttribute("url", "/");

                success = 'n';
                cause = "탈퇴 회원 로그인";
                url = "alert";
            default:
                session.setAttribute("mid", mid);

                //프로필 사진 불러오기
                ResMemberDto.Member member = memberService.findMember( mid );
                session.setAttribute("profile", member.getProfile() == null ? null : imageService.findProfile( mid, member.getProfile(), memberService.findUpdateHistory( mid, 'I' ) ));
                session.setAttribute("nickName", member.getNickName());

                success = 'y';
                url = "redirect:/home";
        }

        //로그인 이력
        ReqMemberDto.LoginLog loginLog = new ReqMemberDto.LoginLog( id, cause, success );
        logService.memberLogIn( loginLog, request );

        return url;
    }

    @GetMapping("/login-fail")
    public String getLogin(  @RequestParam(value = "error", required = false)String error, @RequestParam(value = "exception", required = false)String exception, @RequestParam(value = "id", required = false) String id,
                                HttpServletRequest request, Model model ) throws SQLException {

        String cause = null;
        switch (error) {
            case "BadInfo":
                cause = "아이디와 비밀번호 불일치";
                break;
            case "NotInfo":
                cause = "계정 미존재";
                break;
            case "SystemError":
                cause = "시스템 문제";
                break;
            case "RefuseAuth":
                cause = "인증요청 거절";
                break;
            case "NotKnow":
                cause = "알 수 없는 오류";
                break;
        }

        ReqMemberDto.LoginLog loginLog = new ReqMemberDto.LoginLog(id, cause, 'n');
        logService.memberLogIn(loginLog, request);

        model.addAttribute("error", error);
        model.addAttribute("exception", exception);

        return "index";
    }

    @GetMapping
    public String getLoginView( )  {
        return "index";
    }

    @GetMapping("/changePwd")
    public String getPwdChangeView() {
        return "/include/popup_pwdChange";
    }

    @ResponseBody
    @PostMapping("/findPwd")
    public int postFindPassword( String password, HttpSession session ) throws Exception {
        logger.debug("입력한 비밀번호({})가 일치하는지 요청", password);
        String mid = (String) session.getAttribute("mid");

        return memberService.findPwd( mid, password);
    }

    @ResponseBody
    @PostMapping("/updatePwd")
    public int postUpdatePassword( ReqMemberDto.Update update, HttpSession session ) {
        logger.debug("기존 비밀번호({})를 수정 요청", update.getPassword());
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
        logger.debug("회원({}) 탈퇴 요청", delete.getId());
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
        logger.debug("탈퇴한 회원({}) 복구 요청", login.getId());
        String mid = memberService.findMid( login.getId(), login.getPassword() );
        memberService.recoverMember( mid, login );

        return "forward:/login";
    }
}
