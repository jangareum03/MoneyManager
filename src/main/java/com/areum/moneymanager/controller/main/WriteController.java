package com.areum.moneymanager.controller.main;

import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.service.ImageService;
import com.areum.moneymanager.service.main.WriteService;
import com.areum.moneymanager.service.main.WriteServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
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
@RequestMapping("/accounts/write")
public class WriteController {

    private static final String INCOME_CODE = "010000";
    private  final WriteService writeService;
    private final ImageService imageService;
    private final Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    public WriteController(WriteServiceImpl writeService, ImageService imageService ) {
        this.writeService = writeService;
        this.imageService = imageService;
    }

    //가계부 작성화면 요청
    @GetMapping("/step1")
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

    //카테고리 요청
    @ResponseBody
    @PostMapping("/category")
    public List<ResServiceDto.Category> postCategoryList( String code ) throws Exception {
        return writeService.getCategory( code );
    }

    //가계부 등록 요청
    @PostMapping
    public String postWrite( ReqServiceDto.Write write, HttpSession session ) throws Exception {
        String mid = (String) session.getAttribute("mid");

        writeService.writeAccountBook(write, mid);
        imageService.uploadImageFile( write, mid );
        return "redirect:/accounts/write/step1";
    }

    //가계부 화면 요청
    @PostMapping("/step2")
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
