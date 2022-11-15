package com.areum.moneymanager.service.member;

import com.areum.moneymanager.dao.MemberInfoDao;
import com.areum.moneymanager.dao.MemberInfoDaoImpl;
import com.areum.moneymanager.dto.ReqMemberInfoDto;
import com.areum.moneymanager.dto.ResMemberInfoDto;
import com.areum.moneymanager.entity.MemberInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;

@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberInfoDao memberInfoDao;
    private final Logger LOGGER = LogManager.getLogger(MemberServiceImpl.class);

    public MemberServiceImpl(MemberInfoDaoImpl memberDao) {
        this.memberInfoDao = memberDao;

    }

    @Override
    public void changePwd(ReqMemberInfoDto.FindPwd findPwdDto, String newPwd) throws SQLException {
        MemberInfo memberInfo = findPwdDto.toEntity();

        memberInfoDao.updatePwd( memberInfo.getId(), newPwd );
        LOGGER.debug("임시 비밀번호 변경: {}", newPwd);
    }

    @Override
    public ResMemberInfoDto.FindId findId(ReqMemberInfoDto.FindId findIdDto ) throws SQLException {
        MemberInfo memberInfo = findIdDto.toEntity();
        LOGGER.debug("아이디 찾기 입력한 값: 이름({}), 이메일({})", memberInfo.getName(), memberInfo.getEmail());

        return ResMemberInfoDto.FindIdResponse( memberInfoDao.selectId( memberInfo.getName() , memberInfo.getEmail() ) );
    }

    @Override
    public String findMid(ReqMemberInfoDto.Login loginDto) throws SQLException {
        MemberInfo memberInfo = loginDto.toEntity();
        LOGGER.debug("회원번호 요청한 아이디: {}, 비밀번호: {}", memberInfo.getId(), memberInfo.getPassword());

        return memberInfoDao.selectMid( memberInfo.getId(), memberInfo.getPassword() );
    }

    @Override
    public ResMemberInfoDto.FindPwd findPwd( ReqMemberInfoDto.FindPwd findPwdDto ) throws SQLException {
        MemberInfo memberInfo = findPwdDto.toEntity();

        LOGGER.debug("비밀번호 찾기 입력한 값: 이름({}), 아이디({})", memberInfo.getName(), memberInfo.getId());
        return ResMemberInfoDto.FindPwdResponse( memberInfoDao.selectEmail(memberInfo.getName(), memberInfo.getId()) );
    }

    @Override
    public int idCheck( String id ) throws SQLException {
        LOGGER.debug("중복확인 요청 아이디: {}", id);
        return memberInfoDao.selectCountById(id);
    }

    @Override
    public void joinMember( ReqMemberInfoDto.Join joinDto ) throws SQLException {
        String mid = makeMemberId(joinDto.getId());
        MemberInfo memberInfo = joinDto.toEntity(mid);

        memberInfoDao.insertMember( memberInfo );
        LOGGER.debug("회원가입 완료 데이터: mid({}), id({}), pwd({}), name({}), nickName({}), email({}), gender({})",
                mid, joinDto.getId(), joinDto.getPassword(), joinDto.getName(), joinDto.getNickName(), joinDto.getEmail(), joinDto.getGender());
    }

    @Override
    public int loginCheck( ReqMemberInfoDto.Login loginDto ) throws SQLException {
        String pwd = memberInfoDao.selectPwd( loginDto.getId() );
        LOGGER.debug("로그인 입력한 아이디({})와 비밀번호({})로 찾은 비밀번호: {}", loginDto.getId(), loginDto.getPwd(), pwd);

        if( pwd.equals(loginDto.getPwd()) ) {
            return 1;
        }else{
            if( pwd.equals("0") ) {
                return 0;
            }
                return -1;
        }
    }

    @Override
    public String makeMemberId( String id ) throws SQLException {
        String makeId = "UA";

        makeId += id.substring(0,1);

        int month = LocalDate.now().getMonthValue();
        makeId += (month < 10) ? "0" + month : month;

        String mid = memberInfoDao.selectMid(makeId);
        if( mid == null ) {
            makeId += "001";
        }else{
            int midNum = Integer.parseInt(mid.substring(5));

            if( midNum++ == 999 ) {
                LOGGER.error("회원번호 생성 에러 발생!! 요청 아이디: {}", id);
                return "에러";
            }else{
                makeId += String.format("%03d", midNum);
            }
        }

        LOGGER.debug("요청 아이디: {}, 생성된 회원번호: {}" , id, makeId);
        return makeId;
    }

    @Override
    public int nickNameCheck( String nickName ) throws SQLException {
        LOGGER.debug("중복확인 요청 닉네임: {}", nickName);
        return memberInfoDao.selectCountByNickName(nickName);
    }

}
