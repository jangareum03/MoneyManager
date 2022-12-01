package com.areum.moneymanager.controller.main;

import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.service.main.DetailServiceImpl;
import com.areum.moneymanager.service.main.WriteService;
import com.areum.moneymanager.service.main.WriteServiceImpl;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class DetailController {

    private final DetailServiceImpl detailService;
    private final WriteService writeService;

    @Autowired
    public DetailController(DetailServiceImpl detailService, WriteServiceImpl writeService ) {
        this.detailService = detailService;
        this.writeService = writeService;
    }

    @RequestMapping( value = "/detailMonth", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView getDetailMonthView(@RequestParam(defaultValue = "all")String mode, ReqServiceDto.MonthSearch monthSearch, HttpSession session, HttpServletRequest request ) throws Exception {
        ModelAndView mav = new ModelAndView();

        Map<String, Object> map = new HashMap<>();
        //날짜 얻기
        List<String> dateList = detailService.makeDate(monthSearch);
        map.put("year", dateList.get(0));
        map.put("month", dateList.get(1));
        map.put("mode", mode);
        map.put("option", monthSearch.getOption() == null ? null : monthSearch.getOption().replace(",", ""));
        //카테고리 리스트 얻기
        Map<String, List<ResServiceDto.category>> categoryMap = writeService.getCategory();
        map.put("parent", categoryMap.get("parent"));
        map.put("income", categoryMap.get("income"));
        map.put("expend", categoryMap.get("expend"));
        map.put("incomeCheck", monthSearch.getCategory() == null ? null : detailService.makeCategoryList( monthSearch.getCategory(), categoryMap.get("income").size()));
        map.put("expendCheck", monthSearch.getCategory() == null ? null : detailService.makeCategoryList( monthSearch.getCategory(), categoryMap.get("expend").size()));
        //월 가계부 리스트 및 가격 얻기
        String mid = (String) session.getAttribute("mid");
        Map<String, Object> accountMap = detailService.accountBookByMonth(mid, mode, monthSearch);
        map.put("list", accountMap.get("list"));
        map.put("totalPrice", accountMap.get("totalPrice"));
        map.put("inPrice", accountMap.get("inPrice"));
        map.put("outPrice", accountMap.get("outPrice"));

        mav.addObject("map", map);
        mav.setViewName("/main/detail_month");
        return mav;
    }

    @ResponseBody
    @PostMapping("/detailMonthChart")
    public JSONObject  getMonthChart( HttpSession session ) throws Exception {
        return detailService.getJsonObject( (String)session.getAttribute("mid") );
    }

    @PostMapping("/deleteAccount")
    public String postDelete( ReqServiceDto.DeleteAccount deleteAccount, HttpSession session ) throws Exception {
        detailService.deleteAccountBook( (String)session.getAttribute("mid"), deleteAccount );

        return "redirect:/detailMonth";
    }


}
