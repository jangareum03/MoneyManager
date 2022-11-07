package com.areum.moneymanager.controller.main;

import com.areum.moneymanager.dto.ReqHomeDto;
import com.areum.moneymanager.dto.ResHomeDto;
import com.areum.moneymanager.service.main.HomeService;
import com.areum.moneymanager.service.main.HomeServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeServiceImpl homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/moveCal")
    public ModelAndView getMoveCalendar(ReqHomeDto.MoveDate moveDate, HttpSession session) throws Exception {
        ModelAndView mav = new ModelAndView();

        //달력값 계산
        LocalDate today = LocalDate.now();
        LocalDate startDate = LocalDate.of(moveDate.getYear(), moveDate.getMonth(), 1);
        int start = startDate.get(ChronoField.DAY_OF_WEEK) == 7 ? 0 : startDate.get(ChronoField.DAY_OF_WEEK);
        int rows = (startDate.lengthOfMonth() + start);
        if( rows%7 == 0 ) {
            rows /= 7;
        }else{
            rows = (rows/7) + 1;
        }

        if( today.getYear() == moveDate.getYear() && today.getMonthValue() == moveDate.getMonth() ) {
            mav.addObject("today", today.getDayOfMonth());
        }

        //회원 출석 리스트 받기
        List<ResHomeDto.AttendCheck> attendList = homeService.confirmAttend(session.getAttribute("mid").toString(), startDate.getYear(), startDate.getMonthValue(), startDate.lengthOfMonth());

        mav.addObject("year", startDate.getYear());
        mav.addObject("month", startDate.getMonthValue());
        mav.addObject("start", start);
        mav.addObject("end", startDate.lengthOfMonth());
        mav.addObject("rows", rows);
        mav.addObject("attendList", attendList);
        mav.setViewName("/main/home");

        return mav;
    }

}
