package com.areum.moneymanager.controller.main;

import com.areum.moneymanager.service.main.DetailService;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Controller
public class DetailController {

    private final DetailService detailService;

    public DetailController(DetailService detailService ) {
        this.detailService = detailService;
    }

    @GetMapping("/detail")
    public ModelAndView getChartView( HttpSession session ) throws Exception {
        ModelAndView mav = new ModelAndView();
        String mid = (String)session.getAttribute("mid");

        mav.addObject("date", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월")));
        mav.addObject("priceList", detailService.monthPrice(mid));
        mav.addObject("list", detailService.detailMonthList( mid ));
        mav.setViewName("/main/chart_month");
        return mav;
    }

    @ResponseBody
    @PostMapping("/monthChart")
    public JSONObject  getMonthChart( HttpSession session ) throws Exception {
        return detailService.getJSONData( (String)session.getAttribute("mid") );
    }

}
