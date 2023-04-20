package com.areum.moneymanager.controller.main;

import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.service.main.DetailService;
import com.areum.moneymanager.service.main.DetailServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final DetailService detailService;
    private final Logger LOGGER = LogManager.getLogger(this.getClass());

    public ApiController( DetailServiceImpl detailService ) {
        this.detailService = detailService;
    }

    //faq.json 파일 요청
    @GetMapping("/faq")
    public List<ResServiceDto.Faq> getFAQ() throws IOException, ParseException {
        List<ResServiceDto.Faq> resultList = new ArrayList<>();

        Reader reader = new FileReader("src/main/resources/json/faq.json");

        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray) parser.parse(reader);

        for (Object json : jsonArray) {
            JSONObject object = (JSONObject) json;

            String question = (String) object.get("question");
            String answer = (String) object.get("answer");

            ResServiceDto.Faq faq = ResServiceDto.Faq.builder().question(question).answer(answer).build();
            resultList.add(faq);
        }

        return resultList;
    }

    //카카오 지도 api 요청
    @GetMapping("/kakao-map")
    public ModelAndView getMapView(){
        LOGGER.info("카카오 지도 API 요청");

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/kakaoApi/kakaoMap");

        return mav;
    }

    //구글차트 요청
    @GetMapping("/chart")
    public JSONObject  getAccountChart( HttpSession session ) throws Exception {
        String mid = (String)session.getAttribute("mid");

        JSONObject result = detailService.makeJsonObject( mid, String.valueOf(session.getAttribute("type")) , (ReqServiceDto.AccountSearch) session.getAttribute("chart"));
        session.removeAttribute("chart");
        session.removeAttribute("type");

        return  result;
    }

}
