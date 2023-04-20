package com.areum.moneymanager.controller.member;


import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.service.ImageService;
import com.areum.moneymanager.service.LogService;
import com.areum.moneymanager.service.member.MailService;
import com.areum.moneymanager.service.member.MemberService;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final ImageService imageService;
    private final LogService logService;
    private final MailService mailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    public MemberController(MemberServiceImpl memberService, ImageService imageService, LogService logService, MailService mailService, BCryptPasswordEncoder bCryptPasswordEncoder ) {
        this.memberService = memberService;
        this.imageService = imageService;
        this.logService = logService;
        this.mailService = mailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //로그인 성공
    @GetMapping("/login-success")
    public String getLogin(@AuthenticationPrincipal ReqMemberDto.AuthMember authMember, Model model, HttpSession session, HttpServletRequest request ) throws Exception {
        String url, cause = null;
        char success;

        String id = authMember.getId();
        String pwd = authMember.getPassword();

        String mid = memberService.findMid( id, pwd );

        int result = memberService.loginCheck( id, pwd );
        switch ( result ) {
            case -2 :
                logger.debug("복구 가능한 아이디({})와 비밀번호({})로 로그인 요청", authMember.getId(), authMember.getPassword());

                ReqMemberDto.Login login = new ReqMemberDto.Login(id, pwd);
                memberService.recoverMember( mid, login );

                model.addAttribute("msg", "탈퇴한지 30일이 지나지 않아 계정을 복구하였습니다. 임시 비밀번호를 가입하신 이메일로 보내드렸으니 로그인해주시길 바랍니다.");
                model.addAttribute("method", "get");
                model.addAttribute("url", "/");

                success = 'y';
                url = "alert";

                String email = memberService.findEmail(id, pwd);
                String newPwd = mailService.sendMail(email, "password");
                memberService.changePwd( id, bCryptPasswordEncoder.encode(newPwd) );
                break;
            case -3:
                logger.debug("복구 불가능한 아이디({})와 비밀번호({})로 로그인 요청", authMember.getId(), authMember.getPassword());

                model.addAttribute("msg", "탈퇴한지 30일이 경과하여 계정을 복구할 수 없습니다. 다시 가입해주시길 바랍니다..");
                model.addAttribute("method", "get");
                model.addAttribute("url", "/");

                success = 'n';
                cause = "탈퇴 회원 로그인";
                url = "alert";
                break;
            default:
                session.setAttribute("mid", mid);

                //프로필 사진 불러오기
                ResMemberDto.Member member = memberService.findMember( mid );
                session.setAttribute("profile", member.getProfile() == null ? null : imageService.findProfile( mid, member.getProfile(), memberService.findUpdateHistory( mid, 'I' ) ));
                session.setAttribute("nickName", member.getNickName());

                success = 'y';
                url = "redirect:/home";
                break;
        }

        //로그인 이력
        ReqMemberDto.LoginLog loginLog = new ReqMemberDto.LoginLog( id, cause, success );
        logService.memberLogIn( loginLog, request );

        return url;
    }

    //로그인 실패
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

    //현재 비밀번호 일치여부 요청
    @ResponseBody
    @PostMapping("/password")
    public int postFindPassword( String password, HttpSession session ) throws Exception {
        String mid = (String) session.getAttribute("mid");
        String nowPwd = memberService.findPwd( mid );

        return bCryptPasswordEncoder.matches( password, nowPwd ) ? 1 : 0;
    }

    //비밀번호 변경
    @ResponseBody
    @PutMapping("/password")
    public int putUpdatePassword( ReqMemberDto.Update update, HttpSession session ) {
        String mid = (String)session.getAttribute("mid");

        try{
            update.encPassword( bCryptPasswordEncoder.encode(update.getPassword()) );
            memberService.modifyMember( mid, update );
            return 1;
        }catch( Exception e ){
            return 0;
        }
    }

    @ResponseBody
    @DeleteMapping
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
}
