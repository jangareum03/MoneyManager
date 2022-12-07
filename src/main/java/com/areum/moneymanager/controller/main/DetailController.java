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

    @RequestMapping( value = "/accountDetail", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView getAccountDetailView(@RequestParam(defaultValue = "all")String mode, @RequestParam(defaultValue = "m")String type, ReqServiceDto.AccountSearch search, HttpSession session ) throws Exception {
        ModelAndView mav = new ModelAndView();

        Map<String, Object> map = new HashMap<>();
        String mid = (String) session.getAttribute("mid");

        //선택한 날짜 옵션별로 년,월,주 설정
        Map<String, Object> accountMap = null;
        if( "y".equals(type) ){
            map.put("year", search.getYear());
            accountMap = detailService.accountBookByYear(mid, mode, search);
        }else if( "w".equals(type) ) {
            List<String> dateList = detailService.makeDate(search);
            map.put("year", dateList.get(0));
            map.put("month", dateList.get(1));
            map.put("week", search.getWeek());
            accountMap = detailService.accountBookByWeek(mid, mode, search);
        }else{
            List<String> dateList = detailService.makeDate(search);
            map.put("year", dateList.get(0));
            map.put("month", dateList.get(1));
            accountMap = detailService.accountBookByMonth( mid, mode, search);
        }

        //카테고리 리스트 얻기
        Map<String, List<ResServiceDto.Category>> categoryMap = writeService.getCategory();
        map.put("parent", categoryMap.get("parent"));
        map.put("income", categoryMap.get("income"));
        map.put("expend", categoryMap.get("expend"));
        map.put("incomeCheck", search.getCategory() == null ? null : detailService.makeCategoryList( search.getCategory(), categoryMap.get("income").size()));
        map.put("expendCheck", search.getCategory() == null ? null : detailService.makeCategoryList( search.getCategory(), categoryMap.get("expend").size()));

        //검색 조건 구분
        map.put("mode", mode);
        map.put("type", type);
        map.put("option", search.getOption() == null ? null : search.getOption().replace(",", ""));

        //가계부 리스트 얻기
        map.put("list", accountMap.get("list"));

        //가격 얻기
        map.put("inPrice", accountMap.get("inPrice"));
        map.put("outPrice", accountMap.get("outPrice"));
        map.put("totalPrice", (int) accountMap.get("inPrice") + (int) accountMap.get("outPrice"));


        //차트를 위한 session 저장
        session.setAttribute("chart", search);
        session.setAttribute("type", type);

        mav.addObject("map", map);
        mav.setViewName("/main/account_list");
        return mav;
    }

    @GetMapping("/datePopup")
    public ModelAndView getPopupView() {
        ModelAndView mav = new ModelAndView();

        mav.addObject("year", LocalDate.now().getYear());
        mav.setViewName("/include/popup_date");
        return mav;
    }

    @ResponseBody
    @PostMapping("/accountChart")
    public JSONObject  postAccountChart( HttpSession session ) throws Exception {
        String mid = (String)session.getAttribute("mid");

        JSONObject result = detailService.getJsonObject( mid, String.valueOf(session.getAttribute("type")) , (ReqServiceDto.AccountSearch) session.getAttribute("chart"));
        session.removeAttribute("chart");
        session.removeAttribute("type");

        return  result;
    }

    @PostMapping("/deleteAccount")
    public String postDelete( ReqServiceDto.DeleteAccount deleteAccount, HttpSession session ) throws Exception {
        detailService.deleteAccountBook( (String)session.getAttribute("mid"), deleteAccount );

        return "redirect:/detailMonth";
    }



}
