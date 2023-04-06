package com.areum.moneymanager.controller.member;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.service.member.MailService;
import com.areum.moneymanager.service.member.MemberService;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;

@Controller
@RequestMapping("/help")
public class HelpController {

    private final MemberService memberService;
    private final MailService mailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public HelpController( MemberServiceImpl memberService, MailService mailService, BCryptPasswordEncoder bCryptPasswordEncoder ) {
        this.memberService = memberService;
        this.mailService = mailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @GetMapping("/id")
    public String getHelpIdView() {
        return "/member/help_id";
    }

    @GetMapping("/password")
    public String getHelpPwdView() {
        return "/member/help_pwd";
    }

    @PostMapping("/find-id")
    public ModelAndView postFindId( ReqMemberDto.FindId findId ) throws Exception {
        ModelAndView mav = new ModelAndView();

        ResMemberDto.FindId member = memberService.findId( findId );
        if(member == null) {
            mav.setViewName("/member/find_id_error");
        }else{
            StringBuilder sb = new StringBuilder(member.getId());
            mav.addObject("id", sb.replace(member.getId().length()-3, member.getId().length(), "***"));

            if( member.getResignDate() == null ) {
                String date = member.getLastDate() == null ? "해당 아이디로 로그인한 적이 없습니다." : "마지막 접속일: " + new SimpleDateFormat("yyyy년 MM월 dd일 E요일 hh시 mm분 ss초").format(member.getLastDate());
                mav.addObject("date", date);
                mav.addObject("msg", "찾으시는 아이디는 아래와 같습니다.<br><br>개인정보를 위해 일부 아이디는 *로 표시됩니다.<br>아이디 전체를 알고 싶으시면 고객센터에 문의해주세요.");
            }else {
                mav.addObject("date", "탈퇴일: " + new SimpleDateFormat("yyyy년 MM월 dd일 E요일 hh시 mm분 ss초").format(member.getResignDate()));

                //30일이 지났는지 날짜 계산
                String today = LocalDate.now().toString();
                String resignDay = member.getResignDate().toString();

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date todayFormat = format.parse(today);
                Date resignDayFormat = format.parse(resignDay);

                long diffDays = ((todayFormat.getTime() - resignDayFormat.getTime()) / 1000) / (24 * 60 * 60);
                if( diffDays > 30 ) {
                    mav.addObject("msg", "찾으시는 아이디는 탈퇴일로부터 30일이 지나 복구 불가능한 아이디입니다.<br>서비스를 이용하시려면 다시 가입해주시길 바랍니다.");
                }else {
                    mav.addObject("msg", "찾으시는 아이디의 현재 상태는 아래와 같습니다.<br><br>탈퇴일로부터 30일이 지나지 않아 복구 가능한 아이디입니다.<br>복구를 원하신다면 로그인을 해주시길 바랍니다.<br>복구를 원하시지 않는다면 30일이 지난 후 다른 아이디로 다시 가입해주시길 바랍니다.");
                }
            }

            mav.setViewName("/member/find_id");
        }

        return mav;
    }

    @PostMapping("/find-password")
    public ModelAndView postFindPwd( ReqMemberDto.FindPwd findPwd ) throws Exception {
        ModelAndView mav = new ModelAndView();

        ResMemberDto.FindPwd member = memberService.findPwd(findPwd);
        if( member == null ) {
            mav.setViewName("/member/find_pwd_error");
        }else{
            if( member.getResignDate() == null ) {
                //임시 비밀번호 전송
                String pwd = mailService.sendMail(member.getEmail(), "password");
                memberService.changePwd( findPwd, bCryptPasswordEncoder.encode(pwd) );

                StringBuilder sb = new StringBuilder(member.getEmail());
                int index = member.getEmail().indexOf("@");
                mav.addObject("email", sb.replace(index -3, index, "***"));
                mav.addObject("msg", "임의의 비밀번호를 아래 이메일로 보내드립니다.<br>해당 이메일로 확인 후 로그인 부탁드립니다.");
                mav.setViewName("/member/find_pwd");
            }else{
                //30일이 지났는지 날짜 계산
                String today = LocalDate.now().toString();
                String resignDay = member.getResignDate().toString();

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date todayFormat = format.parse(today);
                Date resignDayFormat = format.parse(resignDay);

                long diffDay = ( (todayFormat.getTime() - resignDayFormat.getTime()) / 1000 ) / (24 * 60 * 60);
                if( diffDay < 30 ) {
                    //임시 비밀번호 전송
                    String pwd = mailService.sendMail(member.getEmail(), "password");
                    memberService.changePwd( findPwd, bCryptPasswordEncoder.encode(pwd) );

                    StringBuilder sb = new StringBuilder(member.getEmail());
                    int index = member.getEmail().indexOf("@");
                    mav.addObject("email", sb.replace(index -3, index, "***"));
                    mav.addObject("msg", "탈퇴일로부터 30일이 지나지 않아 복구 가능한 아이디로, 임의의 비밀번호를 아래 이메일로 보내드렸습니다.<br>해당 이메일로 확인 후 로그인 하시면 복구 가능합니다.");
                    mav.setViewName("/member/find_pwd");
                }else {
                    mav.addObject("msg", "복구 불가능한 계정으로 비밀번호를 찾을 수 없습니다.");
                    mav.addObject("method", "get");
                    mav.addObject("url", "/helpPwd");
                    mav.setViewName("alert");
                }
            }
        }

        return mav;
    }



}
