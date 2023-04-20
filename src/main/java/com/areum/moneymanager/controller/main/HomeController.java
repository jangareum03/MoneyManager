package com.areum.moneymanager.controller.main;

import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.service.main.HomeService;
import com.areum.moneymanager.service.main.HomeServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;

    private final Logger LOGGER = LogManager.getLogger(this.getClass());

    public HomeController(HomeServiceImpl homeService) {
        this.homeService = homeService;
    }

    //홈화면 요청
    @GetMapping
    public ModelAndView getHomeView(HttpSession session ) throws Exception {
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
        String mid = (String) session.getAttribute("mid");
        List<ResMemberDto.AttendCheck> attendList = homeService.completeAttend(mid, today.getYear(), today.getMonthValue(), today.lengthOfMonth());

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

    //출석체크 요청
    @ResponseBody
    @PostMapping("/attendanceCheck")
    public int getAttendanceCheck( HttpSession session, String day ) throws Exception {
        String mid = (String) session.getAttribute("mid");
        int selectDay = Integer.parseInt(day);
        LocalDate date = LocalDate.now();

        //마지막 출석날짜
        List<ResMemberDto.AttendCheck> attendList = homeService.completeAttend(mid, date.getYear(), date.getMonthValue(), date.lengthOfMonth() );
        int lastDay = 0;
        if( attendList.size() != 0 ) {
            lastDay = Integer.parseInt(attendList.get(0).getDate());
        }

        if( lastDay == selectDay ) {
            LOGGER.info("오늘 출석 완료하여 출석체크 불가");
            return 0;
        }else if( selectDay != date.getDayOfMonth() ) {
            LOGGER.info("선택한 날짜가 오늘이 아니라서 출석체크 불가");
            return -1;
        }else {
            LOGGER.debug("출석체크 가능");
            homeService.doAttend(mid);
            homeService.getPoint(mid);

            return 1;
        }
    }

    //년도 이동 요청
    @GetMapping("/moveCal")
    public ModelAndView getMoveCalendar(ReqServiceDto.MoveDate moveDate, HttpSession session ) throws Exception {
        LOGGER.debug("출석 조회할 날짜(년: {}, 월: {}) 요청", moveDate.getYear(), moveDate.getMonth());
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
        List<ResMemberDto.AttendCheck> attendList = homeService.completeAttend(session.getAttribute("mid").toString(), startDate.getYear(), startDate.getMonthValue(), startDate.lengthOfMonth());

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
