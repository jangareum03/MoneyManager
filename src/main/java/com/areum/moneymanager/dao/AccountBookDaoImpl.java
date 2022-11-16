package com.areum.moneymanager.dao;

import com.areum.moneymanager.entity.AccountBook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Repository
public class AccountBookDaoImpl implements AccountBookDao {

    private final JdbcTemplate jdbcTemplate;

    //쿼리문
    private final String SELECT_ACCOUNT_MONTH = "SELECT tc.NAME category, NVL(SUM(PRICE), 0) price " +
                                                                                            "FROM TB_ACCOUNT_BOOK tab RIGHT JOIN " +
                                                                                                    "(SELECT * FROM TB_CATEGORY WHERE PARENT_CODE = '020000')tc " +
                                                                                                "ON tc.CODE = tab.CATEGORY_ID AND tab.MEMBER_ID = ? AND tab.ACCOUNT_DATE > TRUNC(SYSDATE, 'MM') AND tab.ACCOUNT_DATE < ADD_MONTHS(TRUNC(SYSDATE,'MM'), 1) " +
                                                                                                "GROUP BY tc.NAME";

    public AccountBookDaoImpl( DataSource dataSource ){
        this.jdbcTemplate = new JdbcTemplate( dataSource );
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
    public List<AccountBook> selectAccountByMonth( String mid ) throws SQLException {
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

}
