package com.areum.moneymanager.controller.member;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.service.ImageService;
import com.areum.moneymanager.service.member.MemberService;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@Controller
public class MyPageController {

    private final MemberService memberService;
    private final ImageService imageService;

    public MyPageController( MemberServiceImpl memberService, ImageService imageService ) {
        this.memberService = memberService;
        this.imageService = imageService;
    }

    @GetMapping("/myPage")
    public String getMyPageView() {
        return "/member/mypage";
    }

    @GetMapping("/myInfo")
    public ModelAndView getMyInfoView( HttpSession session ) throws SQLException {
        ModelAndView mav = new ModelAndView();

        String mid = (String)session.getAttribute("mid");
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
        return mav;
    }

    @PostMapping("/updateInfo")
    public String postUpdateInfo( ReqMemberDto.Update update, HttpSession session ) throws Exception {
        String mid = (String)session.getAttribute("mid");

        memberService.modifyMember( mid , update );

        //프로필 이미지 저장
        if( update.getProfile() != null ) {
            ResMemberDto.ProfileHistory profileHistory = memberService.findUpdateHistory(mid, 'I');

            if (profileHistory != null) {
                imageService.uploadProfile(mid, profileHistory, update.getProfile());
            }
        }

        return "redirect:/myInfo";
    }
}
