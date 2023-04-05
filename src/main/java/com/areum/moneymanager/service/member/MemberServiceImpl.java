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
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
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

        memberDao.updateResignMember( deleteMember, "tmi", makeUpdateSQL(deleteMember, "deleteTmi") );
        int result = memberDao.updateResignMember(deleteMember, "tm", makeUpdateSQL(deleteMember, "deleteTm"));

        String sql = "INSERT INTO tb_update_history VALUES(seq_updateHistory.NEXTVAL, ?, ?, SYSDATE, ?, ?, ?, ?, ?)";
        if( result == 1 ) {
            updateHistory.updateSuccess('y');
        }
        memberDao.insertUpdateHistory( mid, updateHistory, sql );
    }

    @Override
    public ResMemberDto.FindId findId( ReqMemberDto.FindId findId ) throws SQLException {
        MemberInfo memberInfo = findId.toEntity();
        MemberInfo result = memberDao.selectId( memberInfo.getName(), memberInfo.getEmail() );

        return result == null? null : ResMemberDto.FindId.toDTO( result );
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
    public ResMemberDto.AuthMember findAuthMember( String id ) {
        return ResMemberDto.AuthMember.toDTO( memberDao.selectById( id ) );
    }

    @Override
    public String findMid( String id, String password ) throws SQLException {
        MemberInfo memberInfo = new ReqMemberDto.Login( id, password ).toEntity();
        LOGGER.debug("[회원번호 유무 확인] 아이디: {}, 비밀번호: {}", memberInfo.getId(), memberInfo.getPassword());

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
    public ResMemberDto.FindPwd findPwd( ReqMemberDto.FindPwd findPwd ) throws SQLException {
        MemberInfo memberInfo = findPwd.toEntity();
        MemberInfo result = memberDao.selectEmail( memberInfo.getName(), memberInfo.getId());

        return result == null ? null : ResMemberDto.FindPwd.toDTO( result );
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

        String sql = "AND member_id IN (SELECT id FROM TB_MEMBER WHERE resign = 'n' OR (resign = 'y' AND restore = 'y')) ";
        return memberDao.selectCountById( id, sql );
    }

    @Override
    public boolean isMember( String id ) {
        return memberDao.selectById(id) != null;
    }

    @Override
    public void joinMember( ReqMemberDto.Join joinDto ) throws SQLException {
        String mid = makeMemberId(joinDto.getId());
        MemberInfo memberInfo = joinDto.toEntity(mid);

        memberDao.insertMember( memberInfo );
        LOGGER.debug("회원가입 완료 데이터: mid({}), id({}), password({}), name({}), nickName({}), email({}), gender({})",
                mid, joinDto.getId(), joinDto.getPassword(), joinDto.getName(), joinDto.getNickName(), joinDto.getEmail(), joinDto.getGender());
    }

    @Override
    public int loginCheck( String id, String password ) throws SQLException {
        int idCount = memberDao.selectCountById( id );

        if( idCount == 1 ) {
            String pwd = memberDao.selectPwd( id );

            if( pwd.equals(password) ) {
                return 1;
            }else {
                if( pwd.equals("null") ) {  //탈퇴한 계정
                    return memberDao.selectResignMember( id, password ) == 1 ? -3 :  -2;
                }
                return -1;
            }
        }else {
            //아이디 존재하지 않음
            return 0;
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

    private String makeSelectSQL( String type ) {
        String sql = null;

        switch ( type ) {
            case "GM" :
                sql = "WHERE member_id IN (SELECT id FROM tb_member WHERE resign = 'n' AND restore IS NULL) ";
                break;
            case "RM" :
                sql = "WHERE member_id IN (SELECT id FROM tb_member WHERE resign = 'y') ";
                break;
            default:
                break;
        }

        return sql;
    }

    private String makeUpdateSQL( MemberInfo memberInfo, String type ) {
        String sql = "";
        switch (type) {
            case "update" :
                sql = "UPDATE tb_member_info SET ";

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
                break;
            case "deleteTmi" :
                sql = "SET resign_date = SYSDATE ";
                break;
            case "deleteTm" :
                sql = "SET resign = 'y', restore = 'y' ";
                break;
            case "recoverTmi" :
                sql = "SET resign_date = NULL ";
                break;
            case "recoverTm" :
                sql = "SET resign = 'n', restore = NULL ";
                break;
            default:
                break;
        }

        return sql;
    }

    @Override
    public void modifyMember( String mid, ReqMemberDto.Update update ) throws SQLException {
        MemberInfo memberUpdate = update.toEntity();
        UpdateHistory updateHistory = findNowMember( memberDao.selectMemberByMid(mid), update );

        //정보수정
        String sql  = makeUpdateSQL( memberUpdate, "update" );
        int updateResult = memberDao.updateMemberInfo( mid, sql );

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

    @Override
    public void recoverMember( String mid, ReqMemberDto.Login login ) throws SQLException {
        MemberInfo memberInfo = login.toEntity( mid );
        UpdateHistory updateHistory = login.toUpdateHistoryEntity( mid );

        memberDao.updateResignMember( memberInfo, "tm", makeUpdateSQL(memberInfo, "recoverTm") );
        int result = memberDao.updateResignMember( memberInfo, "tmi", makeUpdateSQL(memberInfo, "recoverTmi") );

        String sql = "INSERT INTO tb_update_history(id, member_id, success, datetime, type, bf_info, af_info) VALUES(seq_updateHistory.NEXTVAL, ?, ?, SYSDATE, ?, ?, ?)";
        if( result == 1 ) {
            updateHistory.updateSuccess('y');
        }

        memberDao.insertUpdateHistory( mid, updateHistory, sql );
    }
}
