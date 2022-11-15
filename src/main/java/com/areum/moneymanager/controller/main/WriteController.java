package com.areum.moneymanager.controller.main;

import com.areum.moneymanager.dto.ReqAccountBookDto;
import com.areum.moneymanager.service.ImageService;
import com.areum.moneymanager.service.member.main.WriteService;
import com.areum.moneymanager.service.member.main.WriteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Controller
@RequestMapping("/write")
public class WriteController {

    private final WriteService writeService;
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

    @PostMapping("/accountBook")
    public String postWrite(ReqAccountBookDto.Write write, HttpSession session ) throws Exception {
        String mid = (String) session.getAttribute("mid");

        writeService.addAccountBook(write, mid);
        imageService.uploadImageFile( write, mid );
        return "redirect:/write";
    }

    @PostMapping
    public ModelAndView postWriteStep2View( String mode, String year, String month, String date ) throws Exception {
        ModelAndView mav = new ModelAndView();
        LocalDate localDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(date));

        mav.addObject("accountDate", localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        mav.addObject("formatDate", localDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일")));
        mav.addObject("maxImage", 2);

        if( mode.equals("1") ) {
            mav.setViewName("/main/write_income");
        }else{
            mav.setViewName("/main/write_expend");
        }

        return mav;
    }
}
