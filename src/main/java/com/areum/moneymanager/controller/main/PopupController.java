package com.areum.moneymanager.controller.main;

import com.areum.moneymanager.service.member.MemberService;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.time.LocalDate;

@Controller
@RequestMapping("/popup")
public class PopupController {

    private final MemberService memberService;

    public PopupController( MemberServiceImpl memberService ) {
        this.memberService = memberService;
    }

    @GetMapping("/member")
    public ModelAndView getDeleteMemberView( HttpSession session ) throws SQLException {
        String id = memberService.findId((String) session.getAttribute("mid"));

        ModelAndView mav = new ModelAndView();
        mav.addObject("id", id);
        mav.setViewName("/include/popup_delete");

        return mav;
    }

    @GetMapping("/dates")
    public ModelAndView getPopupView() {
        ModelAndView mav = new ModelAndView();

        mav.addObject("year", LocalDate.now().getYear());
        mav.setViewName("/include/popup_date");
        return mav;
    }

    @GetMapping("/password")
    public String getPwdChangeView() {
        return "/include/popup_pwdChange";
    }

}
