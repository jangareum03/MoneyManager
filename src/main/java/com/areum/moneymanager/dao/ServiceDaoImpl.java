package com.areum.moneymanager.dao;

import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import com.areum.moneymanager.entity.AccountBook;
import com.areum.moneymanager.entity.Category;
import com.areum.moneymanager.entity.Notice;
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
    public List<ResServiceDto.ListAccount> selectAccountByCategory( String mid, String startDate, String endDate, String category ) throws SQLException {
        StringBuilder query = new StringBuilder(
                "SELECT tab.id, tab.account_date, tab.fix, SUBSTR(tab.category_id, 0, 2) code, tc.name, tab.title, tab.price " +
                        "FROM tb_account_book tab, tb_category tc " +
                        "WHERE member_id=? AND tc.code = tab.category_id AND account_date BETWEEN TO_DATE(?, 'YYYYMMDD') AND TO_DATE(?, 'YYYYMMDD') "
        );
        query.append(category);
        query.append("ORDER BY account_date DESC");
        return jdbcTemplate.query(
                query.toString(),
                new RowMapper<ResServiceDto.ListAccount>() {
                    @Override
                    public ResServiceDto.ListAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return ResServiceDto.ListAccount.builder().id(rs.getLong("id")).date(rs.getString("account_date")).fix(rs.getString("fix"))
                                .code(rs.getString("code")).name(rs.getString("name")).title(rs.getString("title")).price(rs.getInt("price")).build();
                    }
                },
                mid, startDate, endDate
        );
    }

    @Override
    public List<ResServiceDto.ListAccount> selectAccountByParentCategory( String mid, String startDate, String endDate, String code ) throws SQLException {
        return jdbcTemplate.query(
                "SELECT tab.id, tab.account_date, tab.fix, SUBSTR(tab.category_id, 0, 2) code, tc.name, tab.title, tab.price " +
                        "FROM tb_account_book tab, ( SELECT name,code FROM tb_category WHERE parent_code = ? ) tc " +
                        "WHERE member_id = ? " +
                        "AND tc.code = tab.category_id " +
                        "AND tab.account_date BETWEEN TO_DATE(?, 'YYYYMMDD') AND TO_DATE(?, 'YYYYMMDD') " +
                        "ORDER BY account_date DESC",
                new RowMapper<ResServiceDto.ListAccount>() {
                    @Override
                    public ResServiceDto.ListAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return ResServiceDto.ListAccount.builder().id(rs.getLong("id")).date(rs.getString("account_date")).fix(rs.getString("fix"))
                                .code(rs.getString("code")).name(rs.getString("name")).title(rs.getString("title")).price(rs.getInt("price")).build();
                    }
                },
                code, mid, startDate, endDate
        );
    }

    @Override
    public List<ResServiceDto.ListAccount> selectAccountByTitle(String mid, String startDate, String endDate, String title) throws SQLException {
        return jdbcTemplate.query(
                "SELECT tab.id, tab.account_date, tab.fix, SUBSTR(tab.category_id, 0, 2) code, tc.name, tab.title, tab.price " +
                        "FROM tb_account_book tab, tb_category tc " +
                        "WHERE member_id=? " +
                        "AND tc.code = tab.category_id " +
                        "AND account_date BETWEEN TO_DATE(?, 'YYYYMMDD') AND TO_DATE(?, 'YYYYMMDD') " +
                        "AND title LIKE ? " +
                        "ORDER BY account_date DESC",
                new RowMapper<ResServiceDto.ListAccount>() {
                    @Override
                    public ResServiceDto.ListAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return ResServiceDto.ListAccount.builder().id(rs.getLong("id")).date(rs.getString("account_date")).fix(rs.getString("fix"))
                                .code(rs.getString("code")).name(rs.getString("name")).title(rs.getString("title")).price(rs.getInt("price")).build();
                    }
                },
                mid, startDate, endDate, '%'+title+'%'
        );
    }

    @Override
    public AccountBook selectAccountOneById( String mid, Long id ) throws SQLException {
        List<AccountBook> resultList =  jdbcTemplate.query(
                "SELECT * FROM tb_account_book WHERE member_id = ? AND id = ?",
                new RowMapper<AccountBook>() {
                    @Override
                    public AccountBook mapRow( ResultSet rs, int rowNum ) throws SQLException {
                        return AccountBook.builder().id(rs.getLong("id")).account_date(rs.getString("account_date"))
                                .fix(rs.getString("fix")).fix_option(rs.getString("fix_option")).category_id(rs.getString("category_id"))
                                .title(rs.getString("title")).content(rs.getString("content")).price(rs.getInt("price")).price_type(rs.getString("price_type"))
                                .image1(rs.getString("image1")).image2(rs.getString("image2")).image3(rs.getString("image3"))
                                .location_name(rs.getString("location_name")).location(rs.getString("location")).build();
                    }
                },
                mid, id
        );

        return resultList.isEmpty() ? null : resultList.get(0);
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
    public Long selectId( String mid ) throws SQLException {
        List<Long> idList = jdbcTemplate.query(
                "SELECT id " +
                        "FROM tb_account_book " +
                        "WHERE member_id = ? " +
                        "ORDER BY id DESC",
                new RowMapper<Long>() {
                    @Override
                    public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getLong("id");
                    }
                },
                mid
        );

        return idList.isEmpty() ? null : idList.get(0);
    }

    @Override
    public Integer selectAllNotice() throws SQLException {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_notice",
                Integer.class
        );
    }

    @Override
    public List<Notice> selectAllNotice( int start, int end ) throws SQLException {
        return jdbcTemplate.query(
                "SELECT tn.* " +
                        "FROM ( " +
                        "SELECT ROWNUM num, tmp.* " +
                        "FROM ( SELECT * FROM tb_notice ORDER BY regdate DESC ) tmp " +
                        ") tn " +
                        "WHERE tn.num BETWEEN ? AND ?",
                new RowMapper<Notice>() {
                    @Override
                    public Notice mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return Notice.builder().id( rs.getString("id") ).type(rs.getString("type").charAt(0)).title(rs.getString("title")).content(rs.getString("content"))
                                .regDate(rs.getDate("regdate")).readCnt(rs.getInt("read_cnt")).build();
                    }
                },
                start, end
        );
    }

    @Override
    public List<ResServiceDto.ListAccount> selectAllAccount( String mid, String startDate, String endDate ) throws SQLException {
        return jdbcTemplate.query(
                "SELECT tab.id, tab.account_date, tab.fix, SUBSTR(tab.category_id, 0, 2) code, tc.name, tab.title, tab.price " +
                        "FROM tb_account_book tab, tb_category tc " +
                        "WHERE member_id = ? " +
                        "AND tc.code = tab.category_id " +
                        "AND account_date BETWEEN TO_DATE(?, 'YYYYMMDD') AND TO_DATE(?, 'YYYYMMDD') " +
                        "ORDER BY account_date DESC",
                new RowMapper<ResServiceDto.ListAccount>() {
                    @Override
                    public ResServiceDto.ListAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return ResServiceDto.ListAccount.builder().id(rs.getLong("id")).date(rs.getString("account_date")).fix(rs.getString("fix")).code(rs.getString("code"))
                                .name(rs.getString("name")).title(rs.getString("title")).price(rs.getInt("price")).build();
                    }
                },
                mid, startDate, endDate
        );
    }

    @Override
    public List<Category> selectAllCategory( String code ) throws SQLException {
        return jdbcTemplate.query(
                "SELECT code, name " +
                            "FROM tb_category " +
                            "START WITH code = ? " +
                            "CONNECT BY PRIOR parent_code = code " +
                            "ORDER BY code",
                new RowMapper<Category>() {
                    @Override
                    public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return Category.builder().code(rs.getString("code")).name(rs.getString("name")).build();
                    }
                },
                code
        );
    }

    @Override
    public List<Category> selectCategory() throws SQLException {
        return jdbcTemplate.query(
                "SELECT name, code " +
                        "FROM  tb_category " +
                        "WHERE parent_code IS NULL " +
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
    public List<Category> selectCategory( String code ) throws SQLException {
        return jdbcTemplate.query(
                "SELECT name, code " +
                        "FROM tb_category " +
                        "WHERE parent_code = ? " +
                        "ORDER BY code",
                new RowMapper<Category>() {
                    @Override
                    public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return Category.builder().name(rs.getString("name")).code(rs.getString("code")).build();
                    }
                },
                code
        );
    }

    @Override
    public List<AccountBook> selectGraphByMonth( String mid, String date ) throws SQLException {
        return jdbcTemplate.query(
                "SELECT CASE WHEN SUBSTR(tc.code, 0, 4) = '0201' THEN '식비' " +
                                                    "WHEN SUBSTR(tc.code, 0, 4) = '0202' THEN '교통' " +
                                                    "WHEN SUBSTR(tc.code, 0, 4) = '0203' THEN '문화생활' " +
                                                    "WHEN SUBSTR(tc.code, 0, 4) = '0204' THEN '미용패션' " +
                                                    "WHEN SUBSTR(tc.code, 0, 4) = '0205' THEN '교육' " +
                                                    "WHEN SUBSTR(tc.code, 0, 4) = '0206' THEN '주거통신' " +
                                                    "WHEN SUBSTR(tc.code, 0, 4) = '0207' THEN '의료' " +
                                                    "WHEN SUBSTR(tc.code, 0, 4) = '0208' THEN '기타' " +
                                        "END AS category, " +
                                        "NVL(SUM(price), 0) price " +
                                "FROM TB_ACCOUNT_BOOK tab RIGHT JOIN " +
                                    "( SELECT * " +
                                        "FROM TB_CATEGORY " +
                                        "WHERE parent_code IS NOT NULL " +
                                        "START WITH parent_code = '020000' " +
                                        "CONNECT BY PRIOR code = parent_code ) tc " +
                                "ON member_id = ? AND tc.code = tab.category_id AND tab.account_date >= TRUNC(TO_DATE(?, 'YYYYMMDD'), 'MM') AND tab.account_date < ADD_MONTHS(TRUNC(TO_DATE(?, 'YYYYMMDD'),'MM'), 1) " +
                                "GROUP BY SUBSTR(tc.code, 0, 4) " +
                                "ORDER BY SUBSTR(tc.code, 0, 4)",
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
    public Notice selectNoticeById( String id ) throws SQLException {
        List<Notice> resultList = jdbcTemplate.query(
                "SELECT * FROM tb_notice WHERE id = ?",
                new RowMapper<Notice>() {
                    @Override
                    public Notice mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return Notice.builder().id( rs.getString("id") ).adminId( rs.getString("admin_id") ).type( rs.getString("type").charAt(0) )
                                .title( rs.getString("title") ).content( rs.getString("content") ).rank( rs.getInt("rank") )
                                .regDate( rs.getDate("regdate") ).modifiedDate( rs.getDate("modified_date") ).readCnt( rs.getInt("read_cnt") ).build();
                    }
                },
                id
        );

        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @Override
    public void updateAccountBook( AccountBook accountBook, String mid ) throws SQLException {
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement pstmt = con.prepareStatement(
                            "UPDATE tb_account_book SET fix=?, fix_option=?, category_id=?, title=?, content=?, price=?, price_type=?, image1=?, location_name=?, location=?, modefied_date=SYSDATE WHERE member_id=? AND id=?"
                        );

                        pstmt.setString(1, accountBook.getFix());
                        pstmt.setString(2, accountBook.getFix_option());
                        pstmt.setString(3, accountBook.getCategory_id());
                        pstmt.setString(4, accountBook.getTitle());
                        pstmt.setString(5, accountBook.getContent());
                        pstmt.setInt(6, accountBook.getPrice());
                        pstmt.setString(7, accountBook.getPrice_type());
                        pstmt.setString(8, accountBook.getImage1());
                        pstmt.setString(9, accountBook.getLocation_name());
                        pstmt.setString(10, accountBook.getLocation());
                        pstmt.setString(11, mid);
                        pstmt.setLong(12, accountBook.getId());

                        return pstmt;
                    }
                }
        );
    }

    @Override
    public void updateReadCount( String id ) throws SQLException {
        jdbcTemplate.update(
                "UPDATE tb_notice " +
                                "SET read_cnt = NVL( read_cnt, 0 ) + 1 " +
                                "WHERE id = ?",
                id
        );
    }

}
