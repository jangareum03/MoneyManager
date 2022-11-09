package com.areum.moneymanager.controller.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;


@Controller
@RequestMapping("/write")
public class WriteController {

    @GetMapping
    public ModelAndView getWriteStep1View(){
        ModelAndView mav = new ModelAndView();
        LocalDate today = LocalDate.now();

        mav.addObject("year", today.getYear());
        mav.addObject("month", today.getMonthValue());
        mav.addObject("date", today.getDayOfMonth());
        mav.addObject("end", today.lengthOfMonth());
        mav.setViewName("/main/write");
        return mav;
    }

    @PostMapping
    public ModelAndView getWriteStep2View( String mode, String year, String month, String date ) throws Exception {
        ModelAndView mav = new ModelAndView();

        mav.addObject("year", year);
        mav.addObject("month", month);
        mav.addObject("date", date);


        if( mode.equals("01") ) {
            mav.setViewName("/main/write_income");
        }else{
            mav.setViewName("/main/write_expend");
        }

        return mav;
    }
}
