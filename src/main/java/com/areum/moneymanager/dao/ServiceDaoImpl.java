package com.areum.moneymanager.dao;

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
    private static final String SELECT_ACCOUNT_MONTH = "SELECT tc.NAME category, NVL(SUM(PRICE), 0) price " +
                                                                                            "FROM TB_ACCOUNT_BOOK tab RIGHT JOIN " +
                                                                                                    "(SELECT * FROM TB_CATEGORY WHERE PARENT_CODE = '020000')tc " +
                                                                                                "ON tc.CODE = tab.CATEGORY_ID AND tab.MEMBER_ID = ? AND tab.ACCOUNT_DATE >= TRUNC(SYSDATE, 'MM') AND tab.ACCOUNT_DATE < ADD_MONTHS(TRUNC(SYSDATE,'MM'), 1) " +
                                                                                                "GROUP BY tc.CODE, tc.NAME " +
                                                                                                "ORDER BY tc.CODE";

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
    public List<AccountBook> selectGraphByMonth( String mid ) throws SQLException {
        return jdbcTemplate.query(
                SELECT_ACCOUNT_MONTH,
                new RowMapper<AccountBook>() {
                    @Override
                    public AccountBook mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return AccountBook.builder().category_id(rs.getString("category")).price(rs.getInt("price")).build();
                    }
                },
                mid
        );
    }

    @Override
    public List<Category> selectIncomeCategory() throws SQLException {
        return jdbcTemplate.query(
                "SELECT NAME, CODE " +
                        "FROM   TB_CATEGORY " +
                        "WHERE  PARENT_CODE = '010000' " +
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
    public List<ResServiceDto.detailMonth> selectMonthAccount( String mid, String startDate, String endDate ) throws SQLException {
        return jdbcTemplate.query(
                "SELECT tab.ID, tab.ACCOUNT_DATE, tab.FIX, SUBSTR(tab.CATEGORY_ID, 0, 2) code, tc.NAME, tab.TITLE, tab.PRICE  " +
                            "FROM TB_ACCOUNT_BOOK tab, TB_CATEGORY tc " +
                            "WHERE MEMBER_ID = ? " +
                            "AND tc.CODE = tab.CATEGORY_ID " +
                            "AND ACCOUNT_DATE BETWEEN TO_DATE(?, 'YYYYMMDD') AND TO_DATE(?, 'YYYYMMDD') " +
                            "ORDER BY ACCOUNT_DATE DESC",
                new RowMapper<ResServiceDto.detailMonth>() {
                    @Override
                    public ResServiceDto.detailMonth mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return ResServiceDto.detailMonth.builder().id(rs.getLong("id")).date(rs.getString("account_date")).fix(rs.getString("fix")).code(rs.getString("code"))
                                .name(rs.getString("name")).title(rs.getString("title")).price(rs.getInt("price")).build();
                    }
                },
                mid, startDate, endDate
        );
    }

    @Override
    public List<ResServiceDto.detailMonth> selectMonthByParentCategory( String mid, String startDate, String endDate, String code ) throws SQLException {
        return jdbcTemplate.query(
                "SELECT tab.ID, tab.ACCOUNT_DATE, tab.FIX, SUBSTR(tab.CATEGORY_ID, 0, 2) code, tc.NAME, tab.TITLE, tab.PRICE " +
                        "FROM TB_ACCOUNT_BOOK tab, ( SELECT name,code FROM tb_category WHERE parent_code = ? ) tc " +
                        "WHERE member_id = ? " +
                        "AND tc.code = tab.category_id " +
                        "AND tab.account_date BETWEEN TO_DATE(?, 'YYYYMMDD') AND TO_DATE(?, 'YYYYMMDD') " +
                        "ORDER BY account_date DESC",
                new RowMapper<ResServiceDto.detailMonth>() {
                    @Override
                    public ResServiceDto.detailMonth mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return ResServiceDto.detailMonth.builder().id(rs.getLong("id")).date(rs.getString("account_date")).fix(rs.getString("fix"))
                                .code(rs.getString("code")).name(rs.getString("name")).title(rs.getString("title")).price(rs.getInt("price")).build();
                    }
                },
                code, mid, startDate, endDate
        );
    }

    @Override
    public List<ResServiceDto.detailMonth> selectMonthByCategory( String mid, String startDate, String endDate, String category ) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT tab.ID, tab.ACCOUNT_DATE, tab.FIX, SUBSTR(tab.CATEGORY_ID, 0, 2) code, tc.NAME, tab.TITLE, tab.PRICE " +
                "FROM tb_account_book tab, tb_category tc " +
                "WHERE member_id=? AND tc.code = tab.category_id AND account_date BETWEEN TO_DATE(?, 'YYYYMMDD') AND TO_DATE(?, 'YYYYMMDD') "
        );
        query.append(category);
        query.append("ORDER BY account_date DESC");
        return jdbcTemplate.query(
                query.toString(),
                new RowMapper<ResServiceDto.detailMonth>() {
                    @Override
                    public ResServiceDto.detailMonth mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return ResServiceDto.detailMonth.builder().id(rs.getLong("id")).date(rs.getString("account_date")).fix(rs.getString("fix"))
                                .code(rs.getString("code")).name(rs.getString("name")).title(rs.getString("title")).price(rs.getInt("price")).build();
                    }
                },
                mid, startDate, endDate
        );
    }

    @Override
    public List<ResServiceDto.detailMonth> selectMonthByTitle(String mid, String startDate, String endDate, String title) throws SQLException {
        return jdbcTemplate.query(
                "SELECT tab.ID, tab.ACCOUNT_DATE, tab.FIX, SUBSTR(tab.CATEGORY_ID, 0, 2) code, tc.NAME, tab.TITLE, tab.PRICE " +
                        "FROM tb_account_book tab, tb_category tc " +
                        "WHERE member_id=? " +
                        "AND tc.code = tab.category_id " +
                        "AND account_date BETWEEN TO_DATE(?, 'YYYYMMDD') AND TO_DATE(?, 'YYYYMMDD') " +
                        "AND title LIKE ? " +
                        "ORDER BY account_date DESC",
                new RowMapper<ResServiceDto.detailMonth>() {
                    @Override
                    public ResServiceDto.detailMonth mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return ResServiceDto.detailMonth.builder().id(rs.getLong("id")).date(rs.getString("account_date")).fix(rs.getString("fix"))
                                .code(rs.getString("code")).name(rs.getString("name")).title(rs.getString("title")).price(rs.getInt("price")).build();
                    }
                },
                mid, startDate, endDate, '%'+title+'%'
        );
    }

    @Override
    public Integer selectMonthPrice( String mid, String startDate, String endDate, String code ) throws SQLException {
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
    public Integer selectMonthTotalPrice(String mid, String startDate, String endDate) throws SQLException {
        return jdbcTemplate.queryForObject(
                "SELECT NVL(SUM(price), 0) price " +
                        "FROM tb_account_book " +
                        "WHERE member_id = ? " +
                        "AND account_date BETWEEN TO_DATE(?, 'YYYYMMDD') AND TO_DATE(?, 'YYYYMMDD')",
                Integer.class,
                mid, startDate, endDate
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
