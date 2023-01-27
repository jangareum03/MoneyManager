package com.areum.moneymanager.controller.main;

import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.service.main.DetailServiceImpl;
import com.areum.moneymanager.service.ImageService;
import com.areum.moneymanager.service.main.WriteService;
import com.areum.moneymanager.service.main.WriteServiceImpl;
import org.json.simple.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;


@Controller
public class DetailController {

    private final DetailServiceImpl detailService;
    private final ImageService imageService;
    private final WriteService writeService;

    @Autowired
    public DetailController(DetailServiceImpl detailService, ImageService imageService, WriteServiceImpl writeService) {
        this.detailService = detailService;
        this.imageService = imageService;
        this.writeService = writeService;
    }

    @GetMapping("/accountDetail/{id}")
    public ModelAndView getAccountDetailView( @PathVariable Long id, HttpSession session ) throws SQLException, ParseException {
        ModelAndView mav = new ModelAndView();

        String mid = (String) session.getAttribute("mid");
        ResServiceDto.DetailAccount account = detailService.getAccountBookById( mid, id );

        //고정 옵션 설정
        String option = "";
        if( account.getFixOption() == null ) {
            option = "";
        }else if( account.getFixOption().equals("y") ) {
            option = "일년";
        } else if ( account.getFixOption().equals("m") ) {
            option = "한달";
        }else if ( account.getFixOption().equals("w") ) {
            option = "일주일";
        }

        //제목
        mav.addObject("date", account.getDate().substring(0, 4) + "년 " + account.getDate().substring(4, 6) + "월 " + account.getDate().substring(6, 8) + "일");

        //가계부 정보
        mav.addObject("id", id);
        mav.addObject("fix", account.getFix());
        mav.addObject("fixOption", option);
        mav.addObject("title", account.getTitle());
        mav.addObject("content", account.getContent());
        mav.addObject("price", account.getPrice());
        mav.addObject("image", account.getImage() == null ? null : imageService.findImage(mid, account));
        mav.addObject("locationName", account.getMapName());
        mav.addObject("location", account.getMapRoad());
        mav.addObject("category", detailService.makeCategory( account.getCategory() ));

        mav.setViewName("/main/account_detail");


        return mav;
    }

    @RequestMapping( value = "/accountList", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView getAccountListView(@RequestParam(defaultValue = "all")String mode, @RequestParam(defaultValue = "m")String type, ReqServiceDto.AccountSearch search, HttpSession session ) throws Exception {
        ModelAndView mav = new ModelAndView();

        Map<String, Object> map = new HashMap<>();
        String mid = (String) session.getAttribute("mid");

        //선택한 날짜 옵션별로 년,월,주 설정
        Map<String, Object> accountMap = null;
        if( "y".equals(type) ){
            map.put("year", search.getYear());
            accountMap = detailService.getAccountBookByYear(mid, mode, search);
        }else if( "w".equals(type) ) {
            List<String> dateList = detailService.makeDate(search);
            map.put("year", dateList.get(0));
            map.put("month", dateList.get(1));
            map.put("week", search.getWeek());
            accountMap = detailService.getAccountBookByWeek(mid, mode, search);
        }else{
            List<String> dateList = detailService.makeDate(search);
            map.put("year", dateList.get(0));
            map.put("month", dateList.get(1));
            accountMap = detailService.getAccountBookByMonth( mid, mode, search);
        }

        //가계부 리스트 얻기
        map.put("list", accountMap.get("list"));

        //카테고리 리스트 얻기
        Map<String, List<ResServiceDto.Category>> categoryMap = detailService.getAccountCategory();
        List<ResServiceDto.Category> incomeList = detailService.getAccountCategory( search.getBasicInCategory() == null ? categoryMap.get("income").get(0).getCode() : search.getBasicInCategory() );
        List<ResServiceDto.Category> exportList = detailService.getAccountCategory( search.getBasicExCategory() == null ? categoryMap.get("export").get(0).getCode() : search.getBasicExCategory() );
        map.put("parent", categoryMap.get("parent"));
        map.put("income", categoryMap.get("income"));
        map.put("export", categoryMap.get("export"));
        map.put("subIncome", incomeList);
        map.put("subExport", exportList);

        //검색 조건 구분
        map.put("mode", mode);
        map.put("type", type);
        map.put("option", search.getOption() == null ? null : search.getOption().replace(",", ""));
        map.put("basic", mode.equals("inCategory") ? search.getBasicInCategory() : search.getBasicExCategory());

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

    @GetMapping("/accountUpdate/{id}")
    public ModelAndView getAccountUpdateView( @PathVariable Long id, HttpSession session ) throws Exception {
        ModelAndView mav = new ModelAndView();

        String mid = (String) session.getAttribute("mid");
        ResServiceDto.DetailAccount account = detailService.getAccountBookById( mid, id );

        //제목
        mav.addObject("date", account.getDate().substring(0, 4) + "년 " + account.getDate().substring(4, 6) + "월 " + account.getDate().substring(6, 8) + "일");
        mav.addObject("accountDate", account.getDate());

        //가계부 정보
        mav.addObject("id", id);
        mav.addObject("fix", account.getFix());
        mav.addObject("fixOption", account.getFixOption());
        mav.addObject("title", account.getTitle());
        mav.addObject("content", account.getContent());
        mav.addObject("price", account.getPrice());
        mav.addObject("priceType" , account.getPriceType());
        mav.addObject("image", account.getImage() == null ? null : imageService.findImage(mid, account));
        mav.addObject("maxImage", 1);
        mav.addObject("locationName", account.getMapName());
        mav.addObject("location", account.getMapRoad());

        //카테고리
        List<ResServiceDto.Category> categoryList = detailService.makeCategory( account.getCategory() );
        mav.addObject("largeCategory", writeService.getCategory());
        mav.addObject("mediumCategory", detailService.getAccountCategory( categoryList.get(0).getCode() ));
        mav.addObject("smallCategory", detailService.getAccountCategory( categoryList.get(1).getCode() ));
        mav.addObject("chooseCategory", categoryList);

        mav.setViewName("/main/account_update");

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

        JSONObject result = detailService.makeJsonObject( mid, String.valueOf(session.getAttribute("type")) , (ReqServiceDto.AccountSearch) session.getAttribute("chart"));
        session.removeAttribute("chart");
        session.removeAttribute("type");

        return  result;
    }

    @ResponseBody
    @PostMapping("/accountCategory")
    public List<ResServiceDto.Category> postAccountCategory( String code ) throws SQLException {
        return detailService.getAccountCategory(code);
    }

    @PostMapping("/accountUpdate/{id}")
    public String postAccountUpdate( ReqServiceDto.UpdateAccount updateAccount, HttpSession session ) throws Exception {
        detailService.updateAccountBook( (String)session.getAttribute("mid"), updateAccount );
        imageService.uploadImageFile( updateAccount, (String)session.getAttribute("mid") );
        return "forward:/accountList";
    }

    @PostMapping("/deleteAccount")
    public String postDelete( ReqServiceDto.DeleteAccount deleteAccount, HttpSession session ) throws Exception {
        detailService.deleteAccountBook( (String)session.getAttribute("mid"), deleteAccount );

        return "forward:/accountList";
    }



}
