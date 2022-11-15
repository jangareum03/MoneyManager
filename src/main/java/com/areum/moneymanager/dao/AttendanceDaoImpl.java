package com.areum.moneymanager.dao;


import com.areum.moneymanager.dto.ReqHomeDto;
import com.areum.moneymanager.entity.Attendance;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AttendanceDaoImpl implements AttendanceDao {

    private final JdbcTemplate jdbcTemplate;

    public AttendanceDaoImpl( DataSource dataSource ){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int insertAttend(String mid, String today) throws SQLException {
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
    public List<Attendance> selectAttendDateList( ReqHomeDto.AttendCheck date ) throws SQLException {
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
    public void updatePoint(String mid, int point) throws SQLException {
        jdbcTemplate.update(
                "UPDATE tb_member_info SET point = point + ? WHERE member_id=?",
                point, mid
        );
    }
}
