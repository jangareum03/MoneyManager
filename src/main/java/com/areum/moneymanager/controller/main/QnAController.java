package com.areum.moneymanager.controller.main;

import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.service.main.QnAService;
import com.areum.moneymanager.service.main.QnAServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@Controller
public class QnAController {

    private final QnAService qnAService;
    private final int QNA_COUNT = 10;
    private final int PAGE_SIZE = 5;

    public QnAController( QnAServiceImpl qnAService ) {
        this.qnAService = qnAService;
    }


    @GetMapping("/qna/detail/{id}")
    public String getQnADetailView( @PathVariable String id, Model model ) throws SQLException {
        ResServiceDto.QnADetail qna = qnAService.findQnADetail(id);

        //답변
        if( qna.getAnswer() == 'y' ) {
            model.addAttribute("answer", qnAService.findAnswer( id, qna.getTitle() ) );
        }
        model.addAttribute("question", qna);

        return "/main/qna_detail";
    }

    @GetMapping("/qna")
    public ModelAndView getQnAView( @RequestParam(defaultValue = "1") String page, ReqServiceDto.QnASearch qnASearch ) throws SQLException {
        ModelAndView mav = new ModelAndView();

        int total = 0;
        ResServiceDto.Page pageInfo;
        List<ResServiceDto.QnA> qnAList;

        //검색 조건에 따른 QnA 리스트 처리
        if( qnASearch.getType() == null || qnASearch.getType().equals("all") ) {
            //페이징 처리
            total = qnAService.countAll();
            pageInfo = new ResServiceDto.Page( total, QNA_COUNT, PAGE_SIZE, Integer.parseInt(page)  );
            qnAList = qnAService.findQnAList( pageInfo, Integer.parseInt(page));
        }else {
            //페이징 처리
            total = qnAService.countSearch( qnASearch );
            pageInfo = new ResServiceDto.Page( total, QNA_COUNT, PAGE_SIZE, Integer.parseInt(page) );
            qnAList = qnAService.findSearchList( qnASearch, pageInfo, Integer.parseInt(page) );
        }

        mav.addObject("qnaList", qnAList);
        mav.addObject("paging", pageInfo);
        mav.addObject("search", qnASearch.getType() == null ? ReqServiceDto.QnASearch.builder().type("all").keyword("").startDate("").endDate("").build() : qnASearch);
        mav.addObject("page", page);
        mav.setViewName("/main/qna_list");

        return mav;
    }


    @ResponseBody
    @PostMapping("/qna/checkMember")
    public int postCheckMember( String id, HttpSession session ) throws SQLException {
        String mid = (String)session.getAttribute("mid");

        return qnAService.isSameQnA( id, mid );
    }
}
