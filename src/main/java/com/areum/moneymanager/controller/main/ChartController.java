package com.areum.moneymanager.controller.main;

import com.areum.moneymanager.service.ChartService;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;


@Controller
public class ChartController {

    private final ChartService chartService;

    public ChartController( ChartService chartService ) {
        this.chartService = chartService;
    }

    @GetMapping("/chart")
    public ModelAndView chartView(){
        return new ModelAndView("/main/chart_basic");
    }

    @GetMapping("/monthChart")
    @ResponseBody
    public JSONObject getChartFruit(HttpSession session ) throws Exception {
        return chartService.getJSONData( (String)session.getAttribute("mid") );
    }
}
