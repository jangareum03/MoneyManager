package com.areum.moneymanager.controller.main;

import com.areum.moneymanager.dto.ReqHomeDto;
import com.areum.moneymanager.dto.ResHomeDto;
import com.areum.moneymanager.service.main.HomeService;
import com.areum.moneymanager.service.main.HomeServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @GetMapping
    public ModelAndView getHomeView( HttpSession session ) throws Exception {
        ModelAndView mav = new ModelAndView();

        //달력값 계산
        LocalDate today = LocalDate.now();
        LocalDate startDate = LocalDate.of(today.getYear(), today.getMonthValue(), 1);
        int start = startDate.get(ChronoField.DAY_OF_WEEK) == 7 ? 0 : startDate.get(ChronoField.DAY_OF_WEEK);
        int rows = (startDate.lengthOfMonth() + start);
        if( rows%7 == 0 ) {
            rows /= 7;
        }else{
            rows = (rows/7) + 1;
        }

        //회원 출석 리스트 받기
        String mid = session.getAttribute("mid").toString();
        List<ResHomeDto.AttendCheck> attendList = homeService.confirmAttend(mid, today.getYear(), today.getMonthValue(), today.lengthOfMonth());

        mav.addObject("year", today.getYear());
        mav.addObject("month", today.getMonthValue());
        mav.addObject("start", start);
        mav.addObject("end", today.lengthOfMonth());
        mav.addObject("today", today.getDayOfMonth());
        mav.addObject("rows", rows);
        mav.addObject("attendList", attendList);
        mav.setViewName("/main/home");

        return mav;
    }

    @GetMapping("/attendCheck")
    @ResponseBody
    public void getAttendCheck( HttpSession session ) throws Exception {
        String mid = session.getAttribute("mid").toString();

        int result = homeService.toAttend(mid);
        if( result == 1 ) {
            homeService.addPoint(mid);
        }
    }

    @GetMapping("/attendOne")
    @ResponseBody
    public String getAttendList( HttpSession session ) throws Exception {
        String mid = session.getAttribute("mid").toString();
        LocalDate date = LocalDate.now();

        List<ResHomeDto.AttendCheck> attendCheckList = homeService.confirmAttend(mid, date.getYear(), date.getMonthValue(), date.lengthOfMonth() );
        return attendCheckList.get(0).getDate();
    }

    @GetMapping("/moveCal")
    public ModelAndView getMoveCalendar( ReqHomeDto.MoveDate moveDate, HttpSession session ) throws Exception {
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
