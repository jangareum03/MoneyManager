package com.areum.moneymanager.service.member;

import com.areum.moneymanager.dao.MemberDao;
import com.areum.moneymanager.dao.MemberDaoImpl;
import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.entity.MemberInfo;
import com.areum.moneymanager.mapper.MemberInfoMapper;
import com.areum.moneymanager.mapper.MemberInfoMapperImpl;
import com.areum.moneymanager.mapper.MemberMapper;
import com.areum.moneymanager.mapper.MemberMapperImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberDao memberDao;
    private final MemberMapper memberMapper;
    private final MemberInfoMapper memberInfoMapper;
    private final Logger LOGGER = LogManager.getLogger(MemberServiceImpl.class);

    @Autowired
    public MemberServiceImpl( MemberDaoImpl memberDao, MemberMapperImpl memberMapper, MemberInfoMapperImpl memberInfoMapper) {
        this.memberDao = memberDao;
        this.memberMapper = memberMapper;
        this.memberInfoMapper = memberInfoMapper;
    }

    @Override
    public ResMemberDto.FindId findId(ReqMemberDto.FindId findIdDto) {
        MemberInfo memberInfo = memberInfoMapper.toEntity(findIdDto);
        LOGGER.debug("변경한 값: 이름({}), 이메일({})", memberInfo.getName(), memberInfo.getEmail());

        return memberDao.selectId(memberInfo.getName(), memberInfo.getEmail());
    }

    @Override
    public int idCheck( String id ) {
        LOGGER.debug("중복확인 요청 아이디: {}", id);
        return memberDao.selectCountById(id);
    }

    @Override
    public void joinMember( ReqMemberDto.Join joinDto, String mid ) throws Exception {

        Member member = memberMapper.toEntity(joinDto);
        MemberInfo memberInfo = memberInfoMapper.toEntity(joinDto);

        memberDao.insertMember( member, memberInfo, mid );
        LOGGER.debug("회원가입 완료 데이터: mid({}), id({}), pwd({}), name({}), nickName({}), email({}), gender({})",
                mid, joinDto.getId(), joinDto.getPassword(), joinDto.getName(), joinDto.getNickName(), joinDto.getEmail(), joinDto.getGender());
    }

    @Override
    public int loginCheck( ReqMemberDto.Login loginDto ) {
        String pwd = memberDao.selectPwd( loginDto.getId() );
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
    public String makeMemberId( String id ) {
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
    public int nickNameCheck( String nickName ) {
        LOGGER.debug("중복확인 요청 닉네임: {}", nickName);
        return memberDao.selectCountByNickName(nickName);
    }

}
