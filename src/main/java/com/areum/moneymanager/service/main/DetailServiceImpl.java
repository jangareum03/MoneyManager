package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.ServiceDao;
import com.areum.moneymanager.dao.ServiceDaoImpl;
import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;


@Service
public class DetailServiceImpl implements DetailService {

    private static final String INCODE_CODE = "010000";
    private static final String EXPORT_CODE = "020000";

    private final ServiceDao serviceDao;
    private LocalDate localDate;

    public DetailServiceImpl(ServiceDaoImpl serviceDao) {
        this.serviceDao = serviceDao;
    }


    @Override
    public ResServiceDto.DetailAccount getAccountBookById( String mid, Long id ) throws SQLException {
        return ResServiceDto.DetailAccount.toDto( serviceDao.selectAccountOneById( mid, id ) );
    }

    @Override
    public Map<String, Object> getAccountBookByMonth(String mid, String mode, ReqServiceDto.AccountSearch search) throws SQLException {
        Map<String, Object> resultMap = new HashMap<>();

        //날짜 설정
        if (search.getYear() == null && search.getMonth() == null) {
            localDate = LocalDate.now().withDayOfMonth(1);
        } else {
            localDate = LocalDate.of(Integer.parseInt(search.getYear()), Integer.parseInt(search.getMonth()), 1);
        }
        String startDate = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String endDate = localDate.withDayOfMonth(localDate.lengthOfMonth()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String category = null;

        switch (mode) {
            case "inout":
                resultMap.put("list", serviceDao.selectAccountByParentCategory(mid, startDate, endDate, search.getOption().replace(",", "")));
                break;
            case "title":
                resultMap.put("list", serviceDao.selectAccountByTitle(mid, startDate, endDate, search.getTitle()));
                break;
            case "inCategory":
                if( search.getInCategory() == null || search.getInCategory().length == 0 ) {
                    category = getAccountCategory( search.getBasicInCategory() ).isEmpty() ? "AND category_id = " : "AND tc.parent_code = ";
                    category += search.getBasicInCategory() + "";
                }else {
                    category = makeCategorySQL( search.getInCategory() );
                }
                resultMap.put("list", serviceDao.selectAccountByCategory( mid, startDate, endDate, category ));
                break;
            case "exCategory":
                if( search.getExCategory() == null || search.getExCategory().length == 0 ) {
                    category = getAccountCategory( search.getBasicExCategory() ).isEmpty() ? "AND category_id = ": "AND tc.parent_code = ";
                    category += search.getBasicExCategory() + "";
                }else{
                    category = makeCategorySQL( search.getExCategory() );
                }
                resultMap.put("list", serviceDao.selectAccountByCategory( mid, startDate, endDate, category ));
                break;
            case "all":
            default:
                resultMap.put("list", serviceDao.selectAllAccount(mid, startDate, endDate));
        }

        resultMap.put("inPrice", serviceDao.selectAccountPrice(mid, startDate, endDate, "01"));
        resultMap.put("outPrice", serviceDao.selectAccountPrice(mid, startDate, endDate, "02"));

        return resultMap;
    }

    @Override
    public Map<String, Object> getAccountBookByWeek(String mid, String mode, ReqServiceDto.AccountSearch search) throws SQLException {
        Map<String, Object> resultMap = new HashMap<>();

        //날짜 설정
        List<String> dateList = makeDateByWeek( search );
        String startDate = dateList.get(0);
        String endDate = dateList.get(1);
        String category = null;

        switch ( mode ) {
            case "inout":
                resultMap.put("list", serviceDao.selectAccountByParentCategory(mid, startDate, endDate, search.getOption().replace(",", "")));
                break;
            case "title":
                resultMap.put("list", serviceDao.selectAccountByTitle(mid, startDate, endDate, search.getTitle()));
                break;
            case "inCategory":
                if( search.getInCategory() == null || search.getInCategory().length == 0 ) {
                    category = getAccountCategory( search.getBasicInCategory() ).isEmpty() ? "AND category_id = " : "AND tc.parent_code = ";
                    category += search.getBasicInCategory() + "";
                }else {
                    category = makeCategorySQL( search.getInCategory() );
                }
                resultMap.put("list", serviceDao.selectAccountByCategory( mid, startDate, endDate, category ));
                break;
            case "exCategory":
                if( search.getExCategory() == null || search.getExCategory().length == 0 ) {
                    category = getAccountCategory( search.getBasicExCategory() ).isEmpty() ? "AND category_id = " : "AND tc.parent_code = ";
                    category += search.getBasicExCategory() + "";
                }else{
                    category = makeCategorySQL( search.getExCategory() );
                }
                resultMap.put("list", serviceDao.selectAccountByCategory( mid, startDate, endDate, category ));
                break;
            case "all" :
            default:
                resultMap.put("list", serviceDao.selectAllAccount( mid, startDate, endDate ));
        }

        resultMap.put("inPrice", serviceDao.selectAccountPrice( mid , startDate, endDate, "01"));
        resultMap.put("outPrice", serviceDao.selectAccountPrice( mid , startDate, endDate, "02"));

        return resultMap;
    }

    @Override
    public Map<String, Object> getAccountBookByYear(String mid, String mode, ReqServiceDto.AccountSearch search) throws SQLException {
        Map<String, Object> resultMap = new HashMap<>();

        String startDate = search.getYear() + "0101";
        String endDate = search.getYear() + "1231";
        String category = null;

        switch (mode) {
            case "inout":
                resultMap.put("list", serviceDao.selectAccountByParentCategory(mid, startDate, endDate, search.getOption().replace(",", "")));
                break;
            case "title":
                resultMap.put("list", serviceDao.selectAccountByTitle(mid, startDate, endDate, search.getTitle()));
                break;
            case "inCategory":
                if( search.getInCategory() == null || search.getInCategory().length == 0 ) {
                    category = getAccountCategory( search.getBasicInCategory() ).isEmpty() ? "AND category_id = " : "AND tc.parent_code = ";
                    category += search.getBasicInCategory() + "";
                }else {
                    category = makeCategorySQL( search.getInCategory() );
                }
                resultMap.put("list", serviceDao.selectAccountByCategory( mid, startDate, endDate, category ));
                break;
            case "exCategory":
                if( search.getExCategory() == null || search.getExCategory().length == 0 ) {
                    category = getAccountCategory( search.getBasicExCategory() ).isEmpty() ? "AND category_id = " : "AND tc.parent_code = ";
                    category += search.getBasicExCategory() + "";
                }else{
                    category = makeCategorySQL( search.getExCategory() );
                }
                resultMap.put("list", serviceDao.selectAccountByCategory( mid, startDate, endDate, category ));
                break;
            case "period":
                resultMap.put("list", serviceDao.selectAllAccount(mid, search.getStart(), search.getEnd()));
                break;
            case "all":
            default:
                resultMap.put("list", serviceDao.selectAllAccount(mid, startDate, endDate));
        }

        resultMap.put("inPrice", serviceDao.selectAccountPrice(mid, startDate, endDate, "01"));
        resultMap.put("outPrice", serviceDao.selectAccountPrice(mid, startDate, endDate, "02"));
        return resultMap;
    }

    @Override
    public Map<String, List<ResServiceDto.Category>> getAccountCategory() throws SQLException {
        Map<String, List<ResServiceDto.Category>> resultMap = new HashMap<>();

        resultMap.put("parent", ResServiceDto.Category.entityToDto(serviceDao.selectCategory()));
        resultMap.put("income", ResServiceDto.Category.entityToDto(serviceDao.selectCategory(INCODE_CODE)));
        resultMap.put("export", ResServiceDto.Category.entityToDto(serviceDao.selectCategory(EXPORT_CODE)));
        return resultMap;
    }

    @Override
    public List<ResServiceDto.Category> getAccountCategory( String code ) throws SQLException {
        return ResServiceDto.Category.entityToDto( serviceDao.selectCategory(code) );
    }

    @Override
    public void deleteAccountBook(String mid, ReqServiceDto.DeleteAccount deleteAccount) throws SQLException {
        String sql = makeDeleteSQL(deleteAccount.getId());
        serviceDao.deleteAccountBook(mid, sql);
    }

    private int findEndDay( int start ) {
        switch ( start ) {
            case 1:
                return 5;
            case 2:
                return 4;
            case 3:
                return 3;
            case 4:
                return 2;
            case 5:
                return  1;
            case 6:
                return 0;
            default:
                return 6;
        }
    }

    //월 기준으로 json 얻기
    private JSONObject getJsonMonth(String mid, ReqServiceDto.AccountSearch search) throws SQLException {
        List<ResServiceDto.MonthChart> list = graphByMonth( mid, search );

        JSONObject data = new JSONObject();

        JSONObject col1 = new JSONObject();
        JSONObject col2 = new JSONObject();
        JSONArray title = new JSONArray();
        col1.put("label", "카테고리");
        col1.put("type", "string");
        col2.put("label", "지출");
        col2.put("type", "number");

        title.add(col1);
        title.add(col2);
        data.put("cols", title);

        JSONArray body = new JSONArray();
        for (ResServiceDto.MonthChart dto : list) {
            JSONObject category = new JSONObject();
            category.put("v", dto.getCategory());

            JSONObject price = new JSONObject();
            price.put("v", dto.getPrice());

            JSONArray row = new JSONArray();
            row.add(category);
            row.add(price);

            JSONObject cell = new JSONObject();
            cell.put("c", row);
            body.add(cell);
        }

        data.put("rows", body);
        return data;
    }

    //주 기준으로 json 얻기
    private JSONObject getJsonWeek(String mid, ReqServiceDto.AccountSearch search) throws SQLException {
        List<ResServiceDto.WeekChart> list = graphByWeek( mid, search );
        JSONObject data = new JSONObject();

        JSONObject col1 = new JSONObject();
        col1.put("label", "주");
        col1.put("type", "string");
        JSONObject col2 = new JSONObject();
        col2.put("label", "수입");
        col2.put("type", "number");
        JSONObject col3 = new JSONObject();
        col3.put("label", "지출");
        col3.put("type", "number");

        JSONArray title = new JSONArray();
        title.add(col1);
        title.add(col2);
        title.add(col3);
        data.put("cols", title);

        JSONArray body = new JSONArray();
        for( ResServiceDto.WeekChart dto : list ) {
            JSONObject week = new JSONObject();
            week.put( "v", dto.getWeek() + "주" );

            JSONObject inPrice = new JSONObject();
            inPrice.put( "v" , dto.getInPrice() );

            JSONObject outPrice = new JSONObject();
            outPrice.put( "v", dto.getOutPrice() );

            JSONArray row = new JSONArray();
            row.add(week);
            row.add(inPrice);
            row.add(outPrice);

            JSONObject cell = new JSONObject();
            cell.put("c", row);
            body.add(cell);
        }

        data.put("rows", body);

        return data;
    }

    //년 기준으로 json 얻기
    private JSONObject getJsonYear(String mid, ReqServiceDto.AccountSearch search) throws SQLException {
        List<ResServiceDto.YearChart> list = serviceDao.selectGraphByYear(mid, search);
        JSONObject data = new JSONObject();

        JSONObject col1 = new JSONObject();
        col1.put("label", "월");
        col1.put("type", "string");
        JSONObject col2 = new JSONObject();
        col2.put("label", "수입");
        col2.put("type", "number");
        JSONObject col3 = new JSONObject();
        col3.put("label", "지출");
        col3.put("type", "number");

        JSONArray title = new JSONArray();
        title.add(col1);
        title.add(col2);
        title.add(col3);
        data.put("cols", title);

        JSONArray body = new JSONArray();
        for (ResServiceDto.YearChart dto : list) {
            JSONObject month = new JSONObject();
            month.put("v", dto.getMonth() + "월");

            JSONObject inPrice = new JSONObject();
            inPrice.put("v", dto.getInPrice());

            JSONObject outPrice = new JSONObject();
            outPrice.put("v", dto.getOutPrice());

            JSONArray row = new JSONArray();
            row.add(month);
            row.add(inPrice);
            row.add(outPrice);

            JSONObject cell = new JSONObject();
            cell.put("c", row);
            body.add(cell);
        }

        data.put("rows", body);
        return data;
    }

    //월 기준으로 그래프 조회
    private List<ResServiceDto.MonthChart> graphByMonth( String mid, ReqServiceDto.AccountSearch search ) throws SQLException {
        List<String> makeDate = makeDate(search);
        String date = makeDate.get(0) + makeDate.get(1) + "01";

        return new ResServiceDto().toResMonthChart(serviceDao.selectGraphByMonth(mid, date));
    }

    //주 기준으로 그래프 조회
    private List<ResServiceDto.WeekChart> graphByWeek( String mid, ReqServiceDto.AccountSearch search ) throws SQLException {
        List<String> makeDate = makeDate(search);
        String date = makeDate.get(0) + makeDate.get(1) + "01";

        return serviceDao.selectGraphByWeek(mid ,date);
    }

    public List<ResServiceDto.Category> makeCategory(String category ) throws SQLException {
        return ResServiceDto.Category.entityToDto(serviceDao.selectAllCategory( category ));
    }

    public String[] makeCategoryList( String[] category, int size ) {
        String[] result = new String[size];

        for (int i = 0; i < size; i++) {
            for (String s : category) {
                if (s.substring(5, 6).equals(i + 1 + "")) {
                    result[i] = s;
                }
            }
        }

        return result;
    }

    private String makeCategorySQL( String[] category ) throws SQLException {
        StringBuilder sql = new StringBuilder("AND category_id IN( ");

        for (int i = 0; i < category.length; i++) {
            sql.append("'").append(category[i]).append("'");

            if (i != (category.length - 1)) {
                sql.append(",");
            }
        }

        sql.append(") ");
        return sql.toString();
    }

    public List<String> makeDate( ReqServiceDto.AccountSearch search ) {
        List<String> result = new ArrayList<>();


        if (search.getYear() == null && search.getMonth() == null) {
            localDate = LocalDate.now();

            result.add(String.valueOf(localDate.getYear()));
        } else {
            localDate = LocalDate.of(Integer.parseInt(search.getYear()), Integer.parseInt(search.getMonth()), 1);
            result.add(String.valueOf(localDate.getYear()));
        }

        if (localDate.getMonthValue() < 10) {
            String month = String.valueOf(localDate.getMonthValue());
            result.add("0" + month);
        } else {
            result.add(String.valueOf(localDate.getMonthValue()));
        }

        return result;
    }

    @Override
    public List<String> makeDateByWeek(ReqServiceDto.AccountSearch search) {
        List<String> result = new ArrayList<>();

        localDate = LocalDate.of(Integer.parseInt(search.getYear()), Integer.parseInt(search.getMonth()), 1);
        String month = localDate.getMonthValue() < 10 ? "0" + localDate.getMonthValue() : "" +localDate.getMonthValue();

        //주 시작 및 종료일
        String startWeek = search.getYear() + month;
        String endWeek = search.getYear() + month;
        int last = localDate.withDayOfMonth(localDate.lengthOfMonth()).getDayOfMonth();

        //날짜 계산 초기값
        int start = 1;
        int end = start + findEndDay( localDate.get(ChronoField.DAY_OF_WEEK) );

        switch (search.getWeek()) {
            case "2":
                start = (end + 1);
                end += 7;
                break;
            case "3":
                start = (end + 1) + 7;
                end += (7 * 2);
                break;
            case "4":
                start = (end + 1) + 14;
                end += (7 * 3);
                break;
            case "5":
                start = (end + 1) + 21;
                end += (7 * 4);
                break;
        }

        //10보다 작은수 0붙이기
        if( start < 10 && end < 10  ){
            result.add(startWeek + "0" + start);
            result.add(endWeek + "0" + end);
        }else if( start < 10 || end < 10 ){
            if( start < 10 ) {
                result.add(startWeek + "0" + start);
                result.add(endWeek + end);
            }else{
                result.add(startWeek + start);
                result.add(endWeek + "0" + end);
            }
        }else {
            //달 마지막 일 검사
            if( end > last ) {
                end = last;
            }
            result.add(startWeek + start);
            result.add(endWeek + end);
        }

        return result;
    }

    private String makeDeleteSQL(Long[] id) {
        StringBuilder sql = new StringBuilder("AND id IN (");

        for (int i = 0; i < id.length; i++) {
            sql.append("'").append(id[i]).append("'");

            if (i != (id.length - 1)) {
                sql.append(",");
            }
        }

        sql.append(")");
        return sql.toString();
    }

    @Override
    public JSONObject makeJsonObject( String mid, String type, ReqServiceDto.AccountSearch search ) throws SQLException {
        if( "y".equals(type) ) {
            return getJsonYear( mid, search );
        }else if( "w".equals(type) ) {
            return getJsonWeek( mid, search );
        }else{
            return getJsonMonth( mid, search );
        }
    }

    @Override
    public void updateAccountBook( String mid, ReqServiceDto.UpdateAccount updateAccount ) throws SQLException {
        serviceDao.updateAccountBook( updateAccount.toEntity(), mid );
    }
}
