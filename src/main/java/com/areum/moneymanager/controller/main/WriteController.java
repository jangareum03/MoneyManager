package com.areum.moneymanager.controller.main;

import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.service.ImageService;
import com.areum.moneymanager.service.main.WriteService;
import com.areum.moneymanager.service.main.WriteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Controller
@RequestMapping("/write")
public class WriteController {

    private static final String INCOME_CODE = "010000";
    private  final WriteService writeService;
    private final ImageService imageService;

    @Autowired
    public WriteController(WriteServiceImpl writeService, ImageService imageService ) {
        this.writeService = writeService;
        this.imageService = imageService;
    }

    @GetMapping("/map")
    public String getMapView(){
        return "/kakaoApi/kakaoMap";
    }


    @GetMapping
    public ModelAndView getWriteStep1View() throws Exception {
        ModelAndView mav = new ModelAndView();
        LocalDate today = LocalDate.now();

        mav.addObject("year", today.getYear());
        mav.addObject("month", today.getMonthValue());
        mav.addObject("date", today.getDayOfMonth());
        mav.addObject("end", today.lengthOfMonth());
        mav.addObject("category", writeService.getCategory());
        mav.setViewName("/main/write");
        return mav;
    }

    @PostMapping("/category")
    @ResponseBody
    public List<ResServiceDto.Category> postCategoryList( String code ) throws Exception {
        return writeService.getCategory( code );
    }

    @PostMapping("/accountBook")
    public String postWrite(ReqServiceDto.Write write, HttpSession session ) throws Exception {
        String mid = (String) session.getAttribute("mid");

        writeService.writeAccountBook(write, mid);
        imageService.uploadImageFile( write, mid );
        return "redirect:/write";
    }

    @PostMapping
    public ModelAndView postWriteStep2View( ReqServiceDto.DivisionAccount divisionAccount ) throws Exception {
        ModelAndView mav = new ModelAndView();
        LocalDate localDate = LocalDate.of(Integer.parseInt(divisionAccount.getYear()), Integer.parseInt(divisionAccount.getMonth()), Integer.parseInt(divisionAccount.getDate()));

        mav.addObject("accountDate", localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        mav.addObject("formatDate", localDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일")));
        mav.addObject("maxImage", 1);
        mav.addObject("category", writeService.getCategory( divisionAccount.getCode() ));

        if( divisionAccount.getCode().equals(INCOME_CODE) ) {
            mav.setViewName("/main/write_income");
        }else{
            mav.setViewName("/main/write_expend");
        }
        return mav;
    }
}
