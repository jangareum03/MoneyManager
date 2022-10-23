package com.areum.moneymanager.service;

import com.areum.moneymanager.dao.MemberDao;
import com.areum.moneymanager.dao.MemberDaoImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberDao memberDao;

    public MemberServiceImpl(MemberDaoImpl memberDao ) {
        this.memberDao = memberDao;
    }

    @Override
    public int idCheck(String id) {
        return memberDao.selectCountById(id);
    }

    @Override
    public String makeMemberId(String id) {
        String makeId = "UA";

        makeId += id.substring(0,1);

        int month = LocalDate.now().getMonthValue();
        makeId += (month < 10) ? "0" + month : month;

        String mid = memberDao.selectMid(makeId);
        if( mid == null ) {
            makeId += "001";
        }else{
            int midNum = Integer.parseInt(mid.substring(5));

            if( midNum++ == 999 ) {
                log.info("에러 페이지로 이동 예정ㅠㅠ");
                return "에러";
            }else{
                makeId += String.format("%03d", midNum);
            }
        }

        return makeId;
    }

    @Override
    public int nickNameCheck(String nickName) {
        return memberDao.selectCountByNickName(nickName);
    }

}
