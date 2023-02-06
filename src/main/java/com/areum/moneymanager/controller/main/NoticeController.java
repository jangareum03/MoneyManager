package com.areum.moneymanager.controller.main;

import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.service.main.NoticeService;
import com.areum.moneymanager.service.main.NoticeServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.List;

@Controller
public class NoticeController {

    private final NoticeService noticeService;
    final int NOTICE_COUNT = 10;
    final int PAGE_SIZE = 5;

    public NoticeController( NoticeServiceImpl noticeService ) {
        this.noticeService = noticeService;
    }

    @GetMapping("/notice/{id}")
    public ModelAndView getNoticeDetail( @PathVariable String id ) throws SQLException {
        ModelAndView mav = new ModelAndView();

        mav.addObject("notice", noticeService.findNotice(id));
        mav.setViewName("/main/notice_detail");

        //조회수 증가
        noticeService.addReadCount( id );

        return mav;
    }

    @GetMapping("/notice")
    public ModelAndView getNoticeView( @RequestParam(defaultValue = "1") String pageIndex ) throws SQLException {
        ModelAndView mav = new ModelAndView();

        //페이징 설정
        int total = noticeService.countAll();
        ResServiceDto.Page page = new ResServiceDto.Page( total, NOTICE_COUNT, PAGE_SIZE, Integer.parseInt(pageIndex) );

        //공지사항 리스트 가져온 후 공지하상 유형 문자열로 변경
        List<ResServiceDto.Notice> noticeList = noticeService.findNoticeList( page, Integer.parseInt(pageIndex) );

        mav.addObject("noticeList", noticeList);
        mav.addObject("page", page);
        mav.setViewName("/main/notice_list");
        return mav;
    }


}
