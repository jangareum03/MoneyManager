package com.areum.moneymanager.dao;

import com.areum.moneymanager.dto.ReqMemberDto;
import com.areum.moneymanager.dto.ResMemberDto;
import com.areum.moneymanager.entity.Attendance;
import com.areum.moneymanager.entity.MemberInfo;
import com.areum.moneymanager.entity.Question;
import com.areum.moneymanager.entity.UpdateHistory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class MemberDaoImpl implements MemberDao {

    private final JdbcTemplate jdbcTemplate;
    private final Logger LOGGER = LogManager.getLogger(MemberDaoImpl.class);

    public MemberDaoImpl(DataSource dataSource ) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int insertAttend( String mid, String today ) throws SQLException {
            return jdbcTemplate.update(
                    new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                            PreparedStatement pstmt = con.prepareStatement("INSERT INTO tb_attendance VALUES(seq_attendance.NEXTVAL, ?, TO_DATE(?, 'YYYYMMDD'))");
                            pstmt.setString(1, mid);
                            pstmt.setString(2, today);

                            return pstmt;
                        }
                    }
            );
    }

    @Override
    public void insertMember( MemberInfo memberInfo ) throws SQLException {
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con ) throws SQLException {
                PreparedStatement pstmt = con.prepareStatement(
                        "INSERT ALL INTO tb_member(id, type) VALUES(?,'A') "
                                + "INTO tb_member_info(member_id, id, password, name, nickname, gender, email) VALUES(?, ?, ?, ?, ?, ?, ?)"
                                + "SELECT * FROM dual");
                pstmt.setString(1, memberInfo.getMemberId());
                pstmt.setString(2, memberInfo.getMemberId());
                pstmt.setString(3, memberInfo.getId());
                pstmt.setString(4, memberInfo.getPassword());
                pstmt.setString(5, memberInfo.getName());
                pstmt.setString(6, memberInfo.getNickName());
                pstmt.setString(7, Character.toString(memberInfo.getGender()));
                pstmt.setString(8, memberInfo.getEmail());

                return pstmt;
            }
        });
    }

    @Override
    public void insertUpdateHistory( String mid, UpdateHistory updateHistory, String sql ) throws SQLException {
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement pstmt = con.prepareStatement(
                        sql
                );

                pstmt.setString(1, mid);
                pstmt.setString(2, Character.toString(updateHistory.getSuccess()));
                pstmt.setString(3, Character.toString(updateHistory.getType()));
                pstmt.setString(4, updateHistory.getBfInfo());
                pstmt.setString(5, updateHistory.getAfInfo());
                if( updateHistory.getDeleteType() != null ) {
                    pstmt.setString(6, updateHistory.getDeleteType());
                    pstmt.setString(7, updateHistory.getDeleteCause());
                }

                return pstmt;
            }
        });
    }

    @Override
    public List<Attendance> selectAttendDateList(ReqMemberDto.AttendCheck date ) throws SQLException {
        return jdbcTemplate.query(
                "SELECT check_date "
                        + "FROM tb_attendance "
                        + "WHERE member_id=? "
                        + "AND check_date BETWEEN TO_DATE(?, 'YYYYMMDD') AND TO_DATE(?, 'YYYYMMDD')"
                        + "ORDER BY check_date DESC",
                new RowMapper<Attendance>() {
                    @Override
                    public Attendance mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return Attendance.builder().checkDate(rs.getDate("check_date")).build();
                    }
                },
                date.getMid(), date.getStartDate(), date.getEndDate()
        );
    }

    @Override
    public MemberInfo selectById( String id ) {
        List<MemberInfo> member = jdbcTemplate.query(
                "SELECT * " +
                        "FROM tb_member_info " +
                        "WHERE id = ?",
                new RowMapper<MemberInfo>() {
                    @Override
                    public MemberInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return MemberInfo.builder().id(rs.getString("id")).name(rs.getString("name")).password(rs.getString("password")).gender(rs.getString("gender").charAt(0)).regDate(rs.getDate("reg_date"))
                                .email(rs.getString("email")).nickName(rs.getString("nickname")).profile(rs.getString("profile")).lastLoginDate(rs.getDate("last_login_date"))
                                .checkCnt(rs.getInt("check_cnt")).build();
                    }
                },
                id
        );
        return member.isEmpty() ? null : member.get(0);
    }

    @Override
    public Integer selectCountById( String id ) throws SQLException {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(id) " +
                                "FROM tb_member_info " +
                                "WHERE id = ? ",
                Integer.class,
                id
        );
    }

    @Override
    public Integer selectCountById( String id, String sql ) throws SQLException {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(id) " +
                        "FROM tb_member_info " +
                        "WHERE id = ? " + sql,
                Integer.class,
                id
        );
    }

    @Override
    public Integer selectCountByNickName( String nickName ) throws SQLException {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(nickname) FROM tb_member_info WHERE nickname=?",
                Integer.class,
                nickName
        );
    }

    @Override
    public MemberInfo selectEmail( String name, String id ) throws SQLException {
        List<MemberInfo> member = jdbcTemplate.query(
                "SELECT email, resign_date " +
                        "FROM tb_member_info " +
                        "WHERE id = ? AND name = ?",
                new RowMapper<MemberInfo>() {
                    @Override
                    public MemberInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return MemberInfo.builder().email(rs.getString("email")).resignDate(rs.getDate("resign_date")).build();
                    }
                },
                id, name
        );

        return member.isEmpty() ? null : member.get(0);
    }

    @Override
    public MemberInfo selectId( String name, String email ) {
        List<MemberInfo> member = jdbcTemplate.query(
                "SELECT id, last_login_date, resign_date " +
                        "FROM tb_member_info " +
                        "WHERE name = ? AND email = ?",
                new RowMapper<MemberInfo>() {
                    @Override
                    public MemberInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return MemberInfo.builder().id(rs.getString(1)).lastLoginDate(rs.getDate(2)).resignDate(rs.getDate(3)).build();
                    }
                },
                name, email
        );

        return member.isEmpty() ? null : member.get(0);
    }

    @Override
    public MemberInfo selectMemberByMid( String mid ) throws SQLException {
        List<MemberInfo> memberList = jdbcTemplate.query(
                "SELECT * " +
                        "FROM tb_member_info tmi, " +
                        "( SELECT id FROM tb_member WHERE id=? ) tm " +
                        "WHERE tmi.member_id = tm.id",
                new RowMapper<MemberInfo>() {
                    @Override
                    public MemberInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return MemberInfo.builder().id(rs.getString("id")).name(rs.getString("name")).password(rs.getString("password")).gender(rs.getString("gender").charAt(0)).regDate(rs.getDate("reg_date"))
                                .email(rs.getString("email")).nickName(rs.getString("nickname")).profile(rs.getString("profile")).lastLoginDate(rs.getDate("last_login_date"))
                                .checkCnt(rs.getInt("check_cnt")).build();
                    }
                },
                mid
        );
        return memberList.isEmpty() ? null : memberList.get(0);
    }


    @Override
    public String selectMid( String mid ) throws SQLException {
        try{
            return jdbcTemplate.queryForObject(
                "SELECT * " +
                            "FROM ( " +
                                "SELECT id " +
                                    "FROM tb_member " +
                                    "WHERE id LIKE '%' || ? || '%' " +
                                    "ORDER BY id DESC ) " +
                            "WHERE ROWNUM <2",
                    String.class,
                    mid
            );
        }catch ( EmptyResultDataAccessException e ) {
            return null;
        }
    }

    @Override
    public String selectMid( String id, String password ) throws SQLException {
        try{
            return jdbcTemplate.queryForObject(
                    "SELECT member_id FROM tb_member_info WHERE id=? AND password=?",
                    String.class,
                    id, password
            );
        }catch( EmptyResultDataAccessException e ) {
            LOGGER.error("결과 쿼리의 값이 0으로 인한 에러 발생!!", e );
            return null;
        }
    }

    @Override
    public String selectPwd( String id) {
        try{
            return jdbcTemplate.queryForObject(
                    "SELECT password " +
                                    "FROM tb_member_info " +
                                    "WHERE id = ? " +
                                        "AND member_id IN (SELECT id FROM tb_member WHERE resign = 'n' AND restore IS NULL)",
                    String.class,
                    id
            );
        }catch ( EmptyResultDataAccessException e ){
            return "null";
        }
    }

    @Override
    public String selectPwdByMid( String mid ) throws SQLException {
        return jdbcTemplate.queryForObject(
                "SELECT password FROM tb_member_info WHERE member_id = ?",
                String .class,
                mid
        );
    }

    @Override
    public Integer selectResignMember( String id, String password ) throws SQLException {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT COUNT(id) " +
                                    "FROM tb_member " +
                                    "WHERE id = (SELECT member_id FROM tb_member_info WHERE id = ? AND password = ? AND resign_date IS NOT NULL) " +
                                        "AND resign = 'y' AND restore = 'n'",
                    Integer.class,
                    id, password
            );
        }catch ( EmptyResultDataAccessException e ) {
            return 0;
        }
    }

    @Override
    public String selectType(String mid) throws SQLException {
        return jdbcTemplate.queryForObject(
                "SELECT type FROM tb_member WHERE id=?",
                String.class,
                mid
        );
    }

    @Override
    public UpdateHistory selectUpdateHistoryByMid( String mid, char type ) throws SQLException {
        List<UpdateHistory> updateHistoryList = jdbcTemplate.query(
                "SELECT * " +
                        "FROM tb_update_history " +
                        "WHERE member_id=? AND type = ? " +
                        "ORDER BY datetime DESC",
                new RowMapper<UpdateHistory>() {
                    @Override
                    public UpdateHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return UpdateHistory.builder().id(rs.getLong("id")).memberId(rs.getString("member_id")).success(rs.getString("success").charAt(0))
                                .datetime(rs.getDate("datetime")).type(rs.getString("type").charAt(0)).bfInfo(rs.getString("bf_info")).afInfo(rs.getString("af_info")).build();
                    }
                },
                mid, type
        );

        return updateHistoryList.isEmpty() ? null : updateHistoryList.get(0);
    }

    @Override
    public int updateMemberInfo( String mid, String sql ) throws SQLException {
        return jdbcTemplate.update(
                sql,
                mid
        );
    }

    @Override
    public int updateResignMember( MemberInfo memberInfo, String table, String sql ) {
        if( table.equals("tmi") ) {
            return jdbcTemplate.update(
                    "UPDATE tb_member_info " +
                            sql +
                            "WHERE id = ? AND password = ? " +
                            "AND member_id = (SELECT id FROM tb_member WHERE id = ?)",
                    memberInfo.getId(), memberInfo.getPassword(), memberInfo.getMemberId()
            );
        }else{
            return jdbcTemplate.update(
                    "UPDATE tb_member "+
                            sql +
                            "WHERE id = ?",
                    memberInfo.getMemberId()
            );
        }
    }

    @Override
    public void updatePoint(String mid, int point) throws SQLException {
        jdbcTemplate.update(
                "UPDATE tb_member_info SET point = point + ? WHERE member_id=?",
                point, mid
        );
    }

    @Override
    public void updatePwd( String id, String password ) throws SQLException {
        jdbcTemplate.update(
                "UPDATE tb_member_info SET password=? WHERE id=?",
                password, id
        );
    }

}
