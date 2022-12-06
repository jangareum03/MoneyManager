package com.areum.moneymanager.dao;

import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.entity.AccountBook;
import com.areum.moneymanager.entity.Category;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
public class ServiceDaoImpl implements ServiceDao {

    private final JdbcTemplate jdbcTemplate;

    //쿼리문
    private static final String SELECT_ACCOUNT_MONTH = "SELECT tc.name category, NVL(SUM(price), 0) price " +
                                                                                                        "FROM tb_account_book tab RIGHT JOIN " +
                                                                                                            "(SELECT * FROM tb_category WHERE parent_code = '020000')tc " +
                                                                                                            "ON tc.code = tab.category_id AND tab.member_id = ? AND tab.account_date >= TRUNC(TO_DATE(?, 'YYYYMMDD'), 'MM') AND tab.account_date < ADD_MONTHS(TRUNC(TO_DATE(?, 'YYYYMMDD'),'MM'), 1) " +
                                                                                                "GROUP BY tc.code, tc.name " +
                                                                                                "ORDER BY tc.code";

    public ServiceDaoImpl(DataSource dataSource ){
        this.jdbcTemplate = new JdbcTemplate( dataSource );
    }


    @Override
    public void deleteAccountBook( String mid, String deleteQuery ) throws SQLException {
        jdbcTemplate.update(
                String.valueOf("DELETE FROM tb_account_book WHERE member_id=? " + deleteQuery),
                mid
        );
    }

    @Override
    public void insertAccountBook(AccountBook accountBook, String mid ) throws SQLException {
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pstmt = con.prepareStatement(
                                "INSERT INTO tb_account_book(id, member_id, category_id, fix, fix_option, account_date, title, content, price, price_type, image1, image2, image3, location_name, location) " +
                                        "VALUES(seq_accountBook.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                        );
                        pstmt.setString(1, mid);
                        pstmt.setString(2, accountBook.getCategory_id());
                        pstmt.setString(3, accountBook.getFix());
                        pstmt.setString(4, accountBook.getFix_option());
                        pstmt.setString(5, accountBook.getAccount_date());
                        pstmt.setString(6, accountBook.getTitle());
                        pstmt.setString(7, accountBook.getContent());
                        pstmt.setInt(8, accountBook.getPrice());
                        pstmt.setString(9, accountBook.getPrice_type());
                        pstmt.setString(10, accountBook.getImage1());
                        pstmt.setString(11, accountBook.getImage2());
                        pstmt.setString(12, accountBook.getImage3());
                        pstmt.setString(13, accountBook.getLocation_name());
                        pstmt.setString(14, accountBook.getLocation());

                        return pstmt;
                    }
                }
        );
    }

    @Override
    public List<Category> selectExpenditureCategory() throws SQLException {
        return jdbcTemplate.query(
                "SELECT name, code " +
                        "FROM tb_category " +
                        "WHERE parent_code = '020000' " +
                        "ORDER BY code",
                new RowMapper<Category>() {
                    @Override
                    public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return Category.builder().name(rs.getString("name")).code(rs.getString("code")).build();
                    }
                }
        );
    }

    @Override
    public List<AccountBook> selectGraphByMonth( String mid, String date ) throws SQLException {
        return jdbcTemplate.query(
                SELECT_ACCOUNT_MONTH,
                new RowMapper<AccountBook>() {
                    @Override
                    public AccountBook mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return AccountBook.builder().category_id(rs.getString("category")).price(rs.getInt("price")).build();
                    }
                },
                mid, date, date
        );
    }

    @Override
    public List<ResServiceDto.WeekChart> selectGraphByWeek( String mid, String date ) throws SQLException {
        return jdbcTemplate.query(
            "WITH TEMP AS (" +
                            "SELECT ROWNUM week, " +
                                "CASE WHEN ROWNUM = 1 THEN firstday " +
                                    "ELSE startweek + (LEVEL-2) * 7 END AS startweek, " +
                                "CASE WHEN ROWNUM = 1 THEN endweek " +
                                    "WHEN ROWNUM IN (4, 5) THEN " +
                                        "(CASE WHEN endweek + (LEVEL-1) * 7 > lastday THEN lastday ELSE endweek + (LEVEL-1) * 7 END)" +
                                    "ELSE endweek + (LEVEL-1) * 7 END AS endweek " +
                            "FROM ( " +
                                "SELECT firstday, lastday, firstday + step AS startweek, firstday + (step-1) AS endweek " +
                                    "FROM ( " +
                                        "SELECT TO_DATE(?, 'YYYYMMDD') firstday, " +
                                                "LAST_DAY(TO_DATE(?, 'YYYYMMDD')) lastday, " +
                                                "CASE WHEN 7 - TO_CHAR(TO_DATE(?, 'YYYYMMDD')- 1, 'D') = 0 THEN 7 " +
                                                    "ELSE 7 - TO_CHAR(TO_DATE(?, 'YYYYMMDD')- 1, 'D') END AS step " +
                                            "FROM DUAL) " +
                                    ") " +
                                    "WHERE ROWNUM < 6 " +
                                    "CONNECT BY LEVEL <= lastday - firstday " +
                        ") " +
                        "SELECT week, " +
                            "(SELECT NVL(SUM(price), 0) " +
                                "FROM TB_ACCOUNT_BOOK tab " +
                                "WHERE member_id = ? AND SUBSTR(category_id, 1, 2) = '01' " +
                                    "AND account_date BETWEEN TO_DATE(TO_CHAR(startweek, 'YYYYMMDD'), 'YYYYMMDD') AND TO_DATE(TO_CHAR(endweek, 'YYYYMMDD'), 'YYYYMMDD') " +
                            ") AS inPrice, " +
                            "(SELECT NVL(SUM(price), 0) " +
                                "FROM TB_ACCOUNT_BOOK tab " +
                                "WHERE member_id = ? AND SUBSTR(category_id, 1, 2) = '02' " +
                                    "AND account_date BETWEEN TO_DATE(TO_CHAR(startweek, 'YYYYMMDD'), 'YYYYMMDD') AND TO_DATE(TO_CHAR(endweek, 'YYYYMMDD'), 'YYYYMMDD') " +
                            ") AS outPrice " +
                        "FROM TEMP",
                new RowMapper<ResServiceDto.WeekChart>() {
                    @Override
                    public ResServiceDto.WeekChart mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return ResServiceDto.WeekChart.builder().week(rs.getInt("week")).inPrice(rs.getInt("inPrice")).outPrice(rs.getInt("outPrice")).build();
                    }
                },
                date, date, date, date, mid, mid
        );
    }

    @Override
    public List<ResServiceDto.YearChart> selectGraphByYear(String mid, ReqServiceDto.AccountSearch search) throws SQLException {
        return jdbcTemplate.query(
                "SELECT basic.month, NVL(SUM(inTab.price), 0) inPrice, NVL(SUM(outTab.price), 0) outPrice " +
                        "FROM (SELECT SUBSTR(account_date, 5,2) month, price " +
                        "FROM tb_account_book tab " +
                        "WHERE member_id = ? AND TO_CHAR(TO_DATE(account_date, 'YYYYMMDD'), 'YYYY') = ? " +
                        "AND SUBSTR(category_id, 1,2) = '01') inTab" +
                        ",(SELECT SUBSTR(account_date, 5,2) month, price " +
                        "FROM tb_account_book tab  " +
                        "WHERE member_id = ? AND TO_CHAR(TO_DATE(account_date, 'YYYYMMDD'), 'YYYY') = ? " +
                        "AND SUBSTR(category_id, 1,2) = '02') outTab" +
                        ",(SELECT TO_CHAR(ADD_MONTHS(TO_DATE('200001', 'YYYYMM'), LEVEL -1), 'MM') month " +
                        "FROM DUAL CONNECT BY LEVEL <= 12) basic " +
                        "WHERE basic.month = inTab.month(+) AND basic.month = outTab.month(+) " +
                        "GROUP BY basic.month, inTab.month, outTab.month " +
                        "ORDER BY 1",
                new RowMapper<ResServiceDto.YearChart>() {
                    @Override
                    public ResServiceDto.YearChart mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return ResServiceDto.YearChart.builder().month(rs.getString("month")).inPrice(rs.getInt("inPrice")).outPrice(rs.getInt("outPrice")).build();
                    }
                },
                mid, search.getYear(), mid, search.getYear()
        );
    }

    @Override
    public List<Category> selectIncomeCategory() throws SQLException {
        return jdbcTemplate.query(
                "SELECT name, code " +
                        "FROM  tb_category " +
                        "WHERE  parent_code = '010000' " +
                        "ORDER BY code",
                new RowMapper<Category>() {
                    @Override
                    public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return Category.builder().name(rs.getString(1)).code(rs.getString(2)).build();
                    }
                }
        );
    }

    @Override
    public List<ResServiceDto.DetailList> selectAllAccount( String mid, String startDate, String endDate ) throws SQLException {
        return jdbcTemplate.query(
                "SELECT tab.id, tab.account_date, tab.fix, SUBSTR(tab.category_id, 0, 2) code, tc.name, tab.title, tab.price " +
                            "FROM tb_account_book tab, tb_category tc " +
                            "WHERE member_id = ? " +
                            "AND tc.code = tab.category_id " +
                            "AND account_date BETWEEN TO_DATE(?, 'YYYYMMDD') AND TO_DATE(?, 'YYYYMMDD') " +
                            "ORDER BY account_date DESC",
                new RowMapper<ResServiceDto.DetailList>() {
                    @Override
                    public ResServiceDto.DetailList mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return ResServiceDto.DetailList.builder().id(rs.getLong("id")).date(rs.getString("account_date")).fix(rs.getString("fix")).code(rs.getString("code"))
                                .name(rs.getString("name")).title(rs.getString("title")).price(rs.getInt("price")).build();
                    }
                },
                mid, startDate, endDate
        );
    }

    @Override
    public List<ResServiceDto.DetailList> selectAccountByParentCategory( String mid, String startDate, String endDate, String code ) throws SQLException {
        return jdbcTemplate.query(
                "SELECT tab.id, tab.account_date, tab.fix, SUBSTR(tab.category_id, 0, 2) code, tc.name, tab.title, tab.price " +
                        "FROM tb_account_book tab, ( SELECT name,code FROM tb_category WHERE parent_code = ? ) tc " +
                        "WHERE member_id = ? " +
                        "AND tc.code = tab.category_id " +
                        "AND tab.account_date BETWEEN TO_DATE(?, 'YYYYMMDD') AND TO_DATE(?, 'YYYYMMDD') " +
                        "ORDER BY account_date DESC",
                new RowMapper<ResServiceDto.DetailList>() {
                    @Override
                    public ResServiceDto.DetailList mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return ResServiceDto.DetailList.builder().id(rs.getLong("id")).date(rs.getString("account_date")).fix(rs.getString("fix"))
                                .code(rs.getString("code")).name(rs.getString("name")).title(rs.getString("title")).price(rs.getInt("price")).build();
                    }
                },
                code, mid, startDate, endDate
        );
    }

    @Override
    public List<ResServiceDto.DetailList> selectAccountByCategory( String mid, String startDate, String endDate, String category ) throws SQLException {
        StringBuilder query = new StringBuilder(
                "SELECT tab.id, tab.account_date, tab.fix, SUBSTR(tab.category_id, 0, 2) code, tc.name, tab.title, tab.price " +
                "FROM tb_account_book tab, tb_category tc " +
                "WHERE member_id=? AND tc.code = tab.category_id AND account_date BETWEEN TO_DATE(?, 'YYYYMMDD') AND TO_DATE(?, 'YYYYMMDD') "
        );
        query.append(category);
        query.append("ORDER BY account_date DESC");
        return jdbcTemplate.query(
                query.toString(),
                new RowMapper<ResServiceDto.DetailList>() {
                    @Override
                    public ResServiceDto.DetailList mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return ResServiceDto.DetailList.builder().id(rs.getLong("id")).date(rs.getString("account_date")).fix(rs.getString("fix"))
                                .code(rs.getString("code")).name(rs.getString("name")).title(rs.getString("title")).price(rs.getInt("price")).build();
                    }
                },
                mid, startDate, endDate
        );
    }

    @Override
    public List<ResServiceDto.DetailList> selectAccountByTitle(String mid, String startDate, String endDate, String title) throws SQLException {
        return jdbcTemplate.query(
                "SELECT tab.id, tab.account_date, tab.fix, SUBSTR(tab.category_id, 0, 2) code, tc.name, tab.title, tab.price " +
                        "FROM tb_account_book tab, tb_category tc " +
                        "WHERE member_id=? " +
                        "AND tc.code = tab.category_id " +
                        "AND account_date BETWEEN TO_DATE(?, 'YYYYMMDD') AND TO_DATE(?, 'YYYYMMDD') " +
                        "AND title LIKE ? " +
                        "ORDER BY account_date DESC",
                new RowMapper<ResServiceDto.DetailList>() {
                    @Override
                    public ResServiceDto.DetailList mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return ResServiceDto.DetailList.builder().id(rs.getLong("id")).date(rs.getString("account_date")).fix(rs.getString("fix"))
                                .code(rs.getString("code")).name(rs.getString("name")).title(rs.getString("title")).price(rs.getInt("price")).build();
                    }
                },
                mid, startDate, endDate, '%'+title+'%'
        );
    }

    @Override
    public Integer selectAccountPrice( String mid, String startDate, String endDate, String code ) throws SQLException {
        return jdbcTemplate.queryForObject(
                "SELECT NVL(SUM(price), 0) price " +
                            "FROM tb_account_book " +
                            "WHERE member_id = ? " +
                                "AND account_date BETWEEN TO_DATE(?, 'YYYYMMDD') AND TO_DATE(?, 'YYYYMMDD') " +
                                "AND SUBSTR(category_id, 0, 2) = ?",
                Integer.class,
                mid, startDate, endDate, code
        );
    }

    @Override
    public List<Category> selectParentCategory() throws SQLException {
        return jdbcTemplate.query(
                "SELECT name, code " +
                        "FROM tb_category " +
                        "WHERE parent_code IS NULL",
                new RowMapper<Category>() {
                    @Override
                    public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return Category.builder().name(rs.getString("name")).code(rs.getString("code")).build();
                    }
                }
        );
    }

}
