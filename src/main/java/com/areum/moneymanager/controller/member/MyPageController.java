package com.areum.moneymanager.controller.member;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.service.ImageService;
import com.areum.moneymanager.service.member.MemberService;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.Objects;

@Controller
@RequestMapping("/members/mypage")
public class MyPageController {

    private final MemberService memberService;
    private final ImageService imageService;
    private final Logger logger= LogManager.getLogger(this.getClass());

    public MyPageController( MemberServiceImpl memberService, ImageService imageService ) {
        this.memberService = memberService;
        this.imageService = imageService;
    }

    @GetMapping
    public ModelAndView getMyPageView( HttpSession session ) throws SQLException {
        String mid = (String)session.getAttribute("mid");

        ModelAndView mav = new ModelAndView();
        mav.addObject("id", memberService.findId(mid));
        mav.setViewName("/member/mypage");

        return mav;
    }

    @GetMapping("/{id}")
    public ModelAndView getMyInfoView(@PathVariable String id, HttpSession session ) throws SQLException {
        ModelAndView mav = new ModelAndView();

        String mid = (String)session.getAttribute("mid");

        try{
            ResMemberDto.Member member = memberService.findMember( mid );

            mav.addObject("profile", member.getProfile() == null ? null : imageService.findProfile( mid, member.getProfile(), memberService.findUpdateHistory( mid, 'I' ) ));
            mav.addObject("badge", memberService.findType( mid ));
            mav.addObject("nickName", member.getNickName());
            mav.addObject("lastLogin", memberService.changeFormatByLastLogin(member.getLastLogin()));
            mav.addObject("name", member.getName());
            mav.addObject("gender", memberService.changeFormatByGender(member.getGender()));
            mav.addObject("joinDate", memberService.changeBasicFormatByDate(member.getJoinDate()));
            mav.addObject("totalAttend", member.getTotalCount());
            mav.addObject("email", member.getEmail());
            mav.setViewName("/member/myInfo");

            session.setAttribute("profile", member.getProfile() == null ? null : imageService.findProfile( mid, member.getProfile(), memberService.findUpdateHistory( mid, 'I' ) ) );

        }catch (Exception e) {
            logger.error(e.getMessage());
        }

        return mav;
    }

    @GetMapping("/faq")
    public String getFAQView() {
        return "/main/faq";
    }

    @PutMapping
    public String putUpdateInfo( ReqMemberDto.Update update, HttpSession session ) throws Exception {
        String mid = (String)session.getAttribute("mid");

        memberService.modifyMember( mid , update );

        //프로필 이미지 저장
        if( update.getProfile() != null ) {
            ResMemberDto.ProfileHistory profileHistory = memberService.findUpdateHistory(mid, 'I');

            if (profileHistory != null) {
                imageService.uploadProfile(mid, profileHistory, update.getProfile());
            }
        }

        return "redirect:/members/mypage";
    }
}
