package com.areum.moneymanager.controller.main;

import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.service.main.QnAService;
import com.areum.moneymanager.service.main.QnAServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/qna")
public class QnAController {

    private final QnAService qnAService;
    private final static int QNA_COUNT = 10;
    private final static int PAGE_SIZE = 5;

    private final Logger LOGGER = LogManager.getLogger(this.getClass());

    public QnAController( QnAServiceImpl qnAService ) {
        this.qnAService = qnAService;
    }


    //Q&A상세 화면 요청
    @GetMapping("/{id}")
    public String getQnADetailView( @PathVariable String id, Model model ) throws SQLException {
        ResServiceDto.QnADetail qna = qnAService.findQnADetail(id);

        //답변
        ResServiceDto.Answer answer = null;
        if( qna.getAnswer() == 'y' ) {
            answer = qnAService.findAnswer( id, qna.getTitle() );
        }
        model.addAttribute("question", qna);
        model.addAttribute("answer", answer);

        return "/main/qna_detail";
    }

    //Q&A 리스트화면 요청
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
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

            LOGGER.debug("선택한 페이지({})에 따른 Q&A 리스트 요청", page);
        }else {
            //페이징 처리
            total = qnAService.countSearch( qnASearch );
            pageInfo = new ResServiceDto.Page( total, QNA_COUNT, PAGE_SIZE, Integer.parseInt(page) );
            qnAList = qnAService.findSearchList( qnASearch, pageInfo, Integer.parseInt(page) );

            LOGGER.debug("선택한 페이지({})와 옵션({}) 따른 Q&A 리스트 요청", page, qnASearch.getType());
        }

        mav.addObject("qnaList", qnAList);
        mav.addObject("paging", pageInfo);
        mav.addObject("search", qnASearch.getType() == null ? ReqServiceDto.QnASearch.builder().type("all").keyword("").startDate("").endDate("").build() : qnASearch);
        mav.addObject("page", page);
        mav.addObject("now", Integer.parseInt(page));
        mav.setViewName("/main/qna_list");

        return mav;
    }

    //Q&A 작성화면 요청
    @GetMapping("/write")
    public String getQnAWriteView() {
        return "/main/qna_write";
    }

    //비밀글 작성자 확인 요청
    @ResponseBody
    @PostMapping("/checkMember")
    public int getCheckMember( String id, HttpSession session ) throws SQLException {
        String mid = (String)session.getAttribute("mid");

        return qnAService.isSameQnA( id, mid );
    }

    //Q&A 작성 요청
    @PostMapping("/write")
    public String postWrite( ReqServiceDto.Question question, HttpSession session ) throws SQLException {
        String mid = (String)session.getAttribute("mid");

        qnAService.registerQnA( question, mid );
        return "redirect:/qna";
    }
}
