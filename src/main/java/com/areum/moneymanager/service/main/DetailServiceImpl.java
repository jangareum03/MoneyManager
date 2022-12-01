package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.ServiceDao;
import com.areum.moneymanager.dao.ServiceDaoImpl;
import com.areum.moneymanager.dto.ReqServiceDto;
import com.areum.moneymanager.dto.ResServiceDto;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class DetailServiceImpl implements DetailService{

    private final ServiceDao serviceDao;
    private LocalDate localDate;

    public DetailServiceImpl(ServiceDaoImpl serviceDao) {
        this.serviceDao = serviceDao;
    }


    public Map<String, Object> accountBookByMonth(String mid, String mode, ReqServiceDto.MonthSearch monthSearch) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        //날짜 설정
        if (monthSearch.getYear() == null && monthSearch.getMonth() == null) {
            localDate = LocalDate.now().withDayOfMonth(1);
        } else {
            localDate = LocalDate.of(Integer.parseInt(monthSearch.getYear()), Integer.parseInt(monthSearch.getMonth()), 1);
        }
        String startDate = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String endDate = localDate.withDayOfMonth(localDate.lengthOfMonth()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        switch (mode) {
            case "all":
                resultMap.put("list", serviceDao.selectMonthAccount(mid, startDate, endDate));
                break;
            case "inout":
                resultMap.put("list", serviceDao.selectMonthByParentCategory(mid, startDate, endDate, monthSearch.getOption().replaceAll(",", "")));
                break;
            case "title":
                resultMap.put("list", serviceDao.selectMonthByTitle(mid, startDate, endDate, monthSearch.getTitle()));
                break;
            case "inCategory":
            case "outCategory":
                if( monthSearch.getCategory() == null || monthSearch.getCategory().length == 0 ) {
                    resultMap.put("list", serviceDao.selectMonthAccount(mid, startDate, endDate));
                }else{
                    String category = makeCategorySQL(monthSearch.getCategory());
                    resultMap.put("list", serviceDao.selectMonthByCategory(mid, startDate, endDate, category));
                }
                break;
        }

        resultMap.put("totalPrice", serviceDao.selectMonthTotalPrice(mid, startDate, endDate));
        resultMap.put("inPrice", serviceDao.selectMonthPrice(mid, startDate, endDate, "01"));
        resultMap.put("outPrice", serviceDao.selectMonthPrice(mid, startDate, endDate, "02"));

        return resultMap;
    }

    //월 기준으로 그래프 조회
    private List<ResServiceDto.MonthChart> graphByMonth(String mid) throws Exception {
        return new ResServiceDto().toResMonthChart(serviceDao.selectGraphByMonth(mid));
    }

    public JSONObject getJsonObject(String mid) throws Exception {
        List<ResServiceDto.MonthChart> list = graphByMonth(mid);

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

    public List<String> makeDate(ReqServiceDto.MonthSearch monthSearch) throws Exception {
        List<String> result = new ArrayList<>();


        if (monthSearch.getYear() == null && monthSearch.getMonth() == null) {
            localDate = LocalDate.now();

            result.add(String.valueOf(localDate.getYear()));
            result.add(String.valueOf(localDate.getMonthValue()));
        } else {
            localDate = LocalDate.of(Integer.parseInt(monthSearch.getYear()), Integer.parseInt(monthSearch.getMonth()), 1);
            result.add(monthSearch.getYear());
            result.add(monthSearch.getMonth());
        }

        return result;
    }

    private String makeCategorySQL(String[] category) throws Exception {
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

    public String[] makeCategoryList( String[] category, int size ) throws Exception {
        String[] result = new String[size];

        for( int i=0; i< result.length; i++ ) {
            for (String s : category) {
                if (s.substring(3, 4).equals(i + 1 + "")) {
                    result[i] = s;
                }
            }
        }

        return result;

    }
}
