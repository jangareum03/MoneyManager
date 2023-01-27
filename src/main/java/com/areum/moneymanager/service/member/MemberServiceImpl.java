package com.areum.moneymanager.service.member;

import com.areum.moneymanager.dao.MemberDao;
import com.areum.moneymanager.dao.MemberDaoImpl;
import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.entity.MemberInfo;
import com.areum.moneymanager.entity.UpdateHistory;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Slf4j
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberDao memberDao;
    private final Logger LOGGER = LogManager.getLogger(MemberServiceImpl.class);

    public MemberServiceImpl(MemberDaoImpl memberDao) {
        this.memberDao = memberDao;

    }

    @Override
    public String changeBasicFormatByDate( Date date ) throws SQLException {
        String originDate = String.valueOf(date);

        int year = Integer.parseInt( originDate.substring(0, 4) );
        int month = Integer.parseInt( originDate.substring(5, 7) );
        int day = Integer.parseInt( originDate.substring(8, 10) );

        return String.format("%d년 %d월 %d일", year, month, day);
    }

    @Override
    public String changeFormatByGender( char gender ) throws SQLException {
        if( gender == 'n' ) {
            return "선택없음";
        }else if( gender == 'm' ) {
            return "남자";
        }else {
            return "여자";
        }
    }

    @Override
    public String changeFormatByLastLogin( Date lastLogin ) throws SQLException {
        String originalDate = String.valueOf(lastLogin);

        int year = Integer.parseInt(originalDate.substring(0, 4));
        int month = Integer.parseInt(originalDate.substring(5,7));
        int date = Integer.parseInt(originalDate.substring(8, 10));
        String day = LocalDate.of(year, month, date).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.KOREA);

        return String.format("마지막 접속일은 %d년 %02d월 %02d일 %s 입니다.", year, month, date, day);
    }

    @Override
    public void changePwd( ReqMemberDto.FindPwd findPwdDto, String newPwd ) throws SQLException {
        MemberInfo memberInfo = findPwdDto.toEntity();

        memberDao.updatePwd( memberInfo.getId(), newPwd );
        LOGGER.debug("임시 비밀번호 변경: {}", newPwd);
    }

    @Override
    public void deleteMember( String mid, ReqMemberDto.Delete delete ) throws SQLException {
        MemberInfo deleteMember = delete.toEntity( mid );
        UpdateHistory updateHistory = delete.toUpdateHistoryEntity( mid );

        memberDao.deleteMember( "tmi", deleteMember );
        int result = memberDao.deleteMember("tm", deleteMember);

        String sql = "INSERT INTO tb_update_history VALUES(seq_updateHistory.NEXTVAL, ?, ?, SYSDATE, ?, ?, ?, ?, ?)";
        if( result == 1 ) {
            updateHistory.updateSuccess('y');
        }
        memberDao.insertUpdateHistory( mid, updateHistory, sql );
    }


    @Override
    public ResMemberDto.FindId findId( ReqMemberDto.FindId findIdDto ) throws SQLException {
        MemberInfo memberInfo = findIdDto.toEntity();
        LOGGER.debug("아이디 찾기 입력한 값: 이름({}), 이메일({})", memberInfo.getName(), memberInfo.getEmail());

        return ResMemberDto.FindIdResponse( memberDao.selectId( memberInfo.getName() , memberInfo.getEmail() ) );
    }

    @Override
    public String findId( String mid ) throws SQLException {
        return memberDao.selectMemberByMid(mid).getId();
    }

    @Override
    public ResMemberDto.Member findMember( String mid ) throws SQLException {
        return ResMemberDto.Member.toDto( memberDao.selectMemberByMid( mid ) );
    }

    @Override
    public String findMid(ReqMemberDto.Login loginDto) throws SQLException {
        MemberInfo memberInfo = loginDto.toEntity();
        LOGGER.debug("회원번호 요청한 아이디: {}, 비밀번호: {}", memberInfo.getId(), memberInfo.getPassword());

        return memberDao.selectMid( memberInfo.getId(), memberInfo.getPassword() );
    }

    private UpdateHistory findNowMember( MemberInfo memberInfo, ReqMemberDto.Update update ) {
        if( update.getName() != null ) {
            return UpdateHistory.builder().success('n').type('N').bfInfo(memberInfo.getName()).afInfo(update.getName()).build();
        }else if( update.getGender() != '\u0000' ) {
            return UpdateHistory.builder().success('n').type('G').bfInfo(String.valueOf(memberInfo.getGender())).afInfo(String.valueOf(update.getGender())).build();
        }else if( update.getEmail() != null ) {
            return UpdateHistory.builder().success('n').type('E').bfInfo(memberInfo.getEmail()).afInfo(update.getEmail()).build();
        }else if( update.getProfile() != null ){
            String profile = memberInfo.getProfile() == null ? " " : memberInfo.getProfile();
            return UpdateHistory.builder().success('n').type('I').bfInfo(profile).afInfo(update.getProfile().getOriginalFilename()).build();
        }else{
            return UpdateHistory.builder().success('n').type('P').bfInfo(memberInfo.getPassword()).afInfo(update.getPassword()).build();
        }
    }

    @Override
    public ResMemberDto.FindPwd findPwd(ReqMemberDto.FindPwd findPwdDto ) throws SQLException {
        MemberInfo memberInfo = findPwdDto.toEntity();

        LOGGER.debug("비밀번호 찾기 입력한 값: 이름({}), 아이디({})", memberInfo.getName(), memberInfo.getId());
        return ResMemberDto.FindPwdResponse( memberDao.selectEmail(memberInfo.getName(), memberInfo.getId()) );
    }

    @Override
    public int findPwd( String mid, String password ) throws SQLException {
        String pwd = memberDao.selectPwdByMid( mid );

        if( pwd.equals(password) ) {
            return 1;
        }else{
            return 0;
        }
    }

    @Override
    public String findType( String mid ) throws SQLException {
        return changeType( memberDao.selectType( mid ) );
    }

    @Override
    public ResMemberDto.ProfileHistory findUpdateHistory( String mid, char type ) throws SQLException {
            return ResMemberDto.ProfileHistory.toDto( memberDao.selectUpdateHistoryByMid( mid, type) );
    }

    private String changeType( String type ) {
        switch (type) {
            case "N":
                return "네이버";
            case "K":
                return "카카오";
            case "G":
                return "구글";
            case "F":
                return "페이스북";
            default:
                return "일반회원";
        }
    }

    @Override
    public int idCheck( String id ) throws SQLException {
        LOGGER.debug("중복확인 요청 아이디: {}", id);
        return memberDao.selectCountById(id);
    }

    @Override
    public void joinMember( ReqMemberDto.Join joinDto ) throws SQLException {
        String mid = makeMemberId(joinDto.getId());
        MemberInfo memberInfo = joinDto.toEntity(mid);

        memberDao.insertMember( memberInfo );
        LOGGER.debug("회원가입 완료 데이터: mid({}), id({}), pwd({}), name({}), nickName({}), email({}), gender({})",
                mid, joinDto.getId(), joinDto.getPassword(), joinDto.getName(), joinDto.getNickName(), joinDto.getEmail(), joinDto.getGender());
    }

    @Override
    public int loginCheck( ReqMemberDto.Login loginDto ) throws SQLException {
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
    public String makeMemberId( String id ) throws SQLException {
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

    private String makeUpdateSQL( MemberInfo memberInfo ) {
        String sql = "UPDATE tb_member_info SET ";

        if( memberInfo.getName() != null ) {
            sql += "name='" + memberInfo.getName() + "' ";
        }else if( memberInfo.getGender() != '\u0000' ) {
            sql += "gender='" + memberInfo.getGender() + "' ";
        }else if( memberInfo.getEmail() != null ) {
            sql += "email='" + memberInfo.getEmail() + "' ";
        }else if( memberInfo.getMemberId() == null && memberInfo.getPassword() != null ) {
            sql += "password='" + memberInfo.getPassword() + "' ";
        }else{
            sql += "profile='" + memberInfo.getProfile() + "' ";
        }

        sql += "WHERE member_id=?";

        return sql;
    }

    @Override
    public void modifyMember( String mid, ReqMemberDto.Update update ) throws SQLException {
        MemberInfo memberUpdate = update.toEntity();
        UpdateHistory updateHistory = findNowMember( memberDao.selectMemberByMid(mid), update );

        //정보수정
        String sql  = makeUpdateSQL( memberUpdate );
        int updateResult = memberDao.updateMember( mid, sql );

        //회원정보 수정내역 추가
        if( updateResult == 1 ) {
            sql = "INSERT INTO tb_update_history(id, member_id, success, datetime, type, bf_info, af_info) VALUES(seq_updateHistory.NEXTVAL, ?, ?, SYSDATE, ?, ?, ?)";
            updateHistory.updateSuccess('y');
        }
        memberDao.insertUpdateHistory( mid, updateHistory, sql );

    }



    @Override
    public int nickNameCheck( String nickName ) throws SQLException {
        LOGGER.debug("중복확인 요청 닉네임: {}", nickName);
        return memberDao.selectCountByNickName(nickName);
    }


}
