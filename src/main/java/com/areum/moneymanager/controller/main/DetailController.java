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
import java.time.LocalDate;
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
    public ModelAndView getDetailMonthView(@RequestParam(defaultValue = "all")String mode, ReqServiceDto.AccountSearch search, HttpSession session, HttpServletRequest request ) throws Exception {
        ModelAndView mav = new ModelAndView();

        Map<String, Object> map = new HashMap<>();
        //날짜 얻기
        List<String> dateList = detailService.makeDate(search);
        map.put("year", dateList.get(0));
        map.put("month", dateList.get(1));
        map.put("mode", mode);
        map.put("option", search.getOption() == null ? null : search.getOption().replace(",", ""));
        //카테고리 리스트 얻기
        Map<String, List<ResServiceDto.Category>> categoryMap = writeService.getCategory();
        map.put("parent", categoryMap.get("parent"));
        map.put("income", categoryMap.get("income"));
        map.put("expend", categoryMap.get("expend"));
        map.put("incomeCheck", search.getCategory() == null ? null : detailService.makeCategoryList( search.getCategory(), categoryMap.get("income").size()));
        map.put("expendCheck", search.getCategory() == null ? null : detailService.makeCategoryList( search.getCategory(), categoryMap.get("expend").size()));
        //월 가계부 리스트 및 가격 얻기
        String mid = (String) session.getAttribute("mid");
        Map<String, Object> accountMap = detailService.accountBookByMonth(mid, mode, search);
        map.put("list", accountMap.get("list"));
        map.put("totalPrice", (int) accountMap.get("inPrice") + (int) accountMap.get("outPrice"));
        map.put("inPrice", accountMap.get("inPrice"));
        map.put("outPrice", accountMap.get("outPrice"));

        //차트를 위한 session 저장
        session.setAttribute("monthChart", search);

        mav.addObject("map", map);
        mav.setViewName("/main/detail_month");
        return mav;
    }

    @RequestMapping( value = "/detailWeek", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView getDetailWeekView( @RequestParam(defaultValue = "all")String mode, ReqServiceDto.AccountSearch search, HttpSession session ) {
        ModelAndView mav = new ModelAndView();

        Map<String, Object> map = new HashMap<>();
        //날짜 얻기


        return mav;
    }

    @RequestMapping( value="/detailYear", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView getDetailYearView( @RequestParam(defaultValue = "all")String mode, ReqServiceDto.AccountSearch search, HttpSession session ) throws Exception {
        ModelAndView mav = new ModelAndView();

        Map<String, Object> map = new HashMap<>();
        //선택한 날짜
        map.put("year", search.getYear());
        map.put("mode", mode);
        map.put("option", search.getOption() == null ? null : search.getOption().replace(",", ""));
        //카테고리 리스트 얻기
        Map<String, List<ResServiceDto.Category>> categoryMap = writeService.getCategory();
        map.put("parent", categoryMap.get("parent"));
        map.put("income", categoryMap.get("income"));
        map.put("expend", categoryMap.get("expend"));
        map.put("incomeCheck", search.getCategory() == null ? null : detailService.makeCategoryList( search.getCategory(), categoryMap.get("income").size()));
        map.put("expendCheck", search.getCategory() == null ? null : detailService.makeCategoryList( search.getCategory(), categoryMap.get("expend").size()));
        //월 가계부 리스트 및 가격 얻기
        String mid = (String) session.getAttribute("mid");
        Map<String, Object> accountMap = detailService.accountBookByYear(mid, mode, search);
        map.put("list", accountMap.get("list"));
        map.put("totalPrice" , (int)accountMap.get("inPrice") + (int)accountMap.get("outPrice"));
        map.put("inPrice", accountMap.get("inPrice"));
        map.put("outPrice", accountMap.get("outPrice"));

        //차트를 위한 session 저장
        session.setAttribute("yearChart", search );

        mav.addObject("map", map);
        mav.setViewName("/main/detail_year");
        return mav;
    }

    @ResponseBody
    @PostMapping("/detailMonthChart")
    public JSONObject  getMonthChart( HttpSession session ) throws Exception {
        JSONObject result = detailService.getJsonMonth( (String)session.getAttribute("mid"), (ReqServiceDto.AccountSearch)session.getAttribute("monthChart") );
        session.removeAttribute("monthChart");

        return  result;
    }

    @ResponseBody
    @PostMapping("/detailYearChart")
    public JSONObject getYearChart( HttpSession session ) throws Exception {
        JSONObject result = detailService.getJsonYear( (String)session.getAttribute("mid"), (ReqServiceDto.AccountSearch) session.getAttribute("yearChart") );
        session.removeAttribute("yearChart");

        return result;
    }

    @GetMapping("/datePopup")
    public ModelAndView getPopupView() {
        ModelAndView mav = new ModelAndView();

        mav.addObject("year", LocalDate.now().getYear());
        mav.setViewName("/include/popup_date");
        return mav;
    }

    @PostMapping("/deleteAccount")
    public String postDelete( ReqServiceDto.DeleteAccount deleteAccount, HttpSession session ) throws Exception {
        detailService.deleteAccountBook( (String)session.getAttribute("mid"), deleteAccount );

        return "redirect:/detailMonth";
    }



}
