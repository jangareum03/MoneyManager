package com.areum.moneymanager.controller.member;


import com.areum.moneymanager.dto.ReqMemberInfoDto;
import com.areum.moneymanager.dto.ResHomeDto;
import com.areum.moneymanager.service.main.HomeService;
import com.areum.moneymanager.service.main.HomeServiceImpl;
import com.areum.moneymanager.service.member.MemberService;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;

@Controller
public class MemberController {

    private final MemberService memberService;
    private final HomeService homeService;
    private final Logger LOGGER = LogManager.getLogger(MemberController.class);

    @Autowired
    public MemberController( MemberServiceImpl memberService, HomeServiceImpl homeService ) {
        this.memberService = memberService;
        this.homeService = homeService;
    }

    @GetMapping("/")
    public String getLoginView( @ModelAttribute("member") ReqMemberInfoDto.Login member ){
        return "index";
    }

    @PostMapping("/login")
    public ModelAndView postLogin(@ModelAttribute("member") ReqMemberInfoDto.Login member, BindingResult bindingResult, HttpSession session) throws Exception {
        ModelAndView mav = new ModelAndView();

        if( !StringUtils.hasText(member.getId()) ) {
            bindingResult.rejectValue("id", "noInput");
        }else if( !StringUtils.hasText(member.getPwd()) ) {
            bindingResult.rejectValue("pwd", "noInput");
        }else{
            int result = memberService.loginCheck( member );
            if( result == 0 ) {
                bindingResult.rejectValue("id", "noMember");
            }else if( result == -1 ) {
                bindingResult.rejectValue("id", "mismatch");
            }
        }

        if( bindingResult.hasErrors() ) {
            mav.setViewName("index");
        }else{
            String mid = memberService.findMid(member);
            session.setAttribute("mid", mid);

            //달력값 계산
            LocalDate today = LocalDate.now();
            LocalDate startDate = LocalDate.of(today.getYear(), today.getMonthValue(), 1);

            int start = startDate.get(ChronoField.DAY_OF_WEEK) == 7 ? 0 : startDate.get(ChronoField.DAY_OF_WEEK);
            int rows = (today.lengthOfMonth() + start);
            if( rows%7 == 0 ) {
                rows /= 7;
            }else{
                rows = (rows/7) + 1;
            }

            //회원 출석 리스트 받기
            List<ResHomeDto.AttendCheck> attendList = homeService.confirmAttend(mid, today.getYear(), today.getMonthValue(), today.lengthOfMonth());

            mav.addObject("year", today.getYear());
            mav.addObject("month", today.getMonthValue());
            mav.addObject("start", start);
            mav.addObject("end", today.lengthOfMonth());
            mav.addObject("today", today.getDayOfMonth());
            mav.addObject("rows", rows);
            mav.addObject("attendList", attendList);
            mav.setViewName("/main/home");
        }

        return mav;
    }

}
