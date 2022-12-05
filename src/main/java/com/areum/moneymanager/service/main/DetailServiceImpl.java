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
public class DetailServiceImpl implements DetailService {

    private final ServiceDao serviceDao;
    private LocalDate localDate;

    public DetailServiceImpl(ServiceDaoImpl serviceDao) {
        this.serviceDao = serviceDao;
    }


    public Map<String, Object> accountBookByMonth(String mid, String mode, ReqServiceDto.AccountSearch search ) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        //날짜 설정
        if (search.getYear() == null && search.getMonth() == null) {
            localDate = LocalDate.now().withDayOfMonth(1);
        } else {
            localDate = LocalDate.of(Integer.parseInt(search.getYear()), Integer.parseInt(search.getMonth()), 1);
        }
        String startDate = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String endDate = localDate.withDayOfMonth(localDate.lengthOfMonth()).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        switch (mode) {
            case "inout":
                resultMap.put("list", serviceDao.selectAccountByParentCategory(mid, startDate, endDate, search.getOption().replaceAll(",", "")));
                break;
            case "title":
                resultMap.put("list", serviceDao.selectAccountByTitle(mid, startDate, endDate, search.getTitle()));
                break;
            case "inCategory":
            case "outCategory":
                if ( search.getCategory() == null || search.getCategory().length == 0 ) {
                    resultMap.put("list", serviceDao.selectAllAccount(mid, startDate, endDate));
                } else {
                    String category = makeCategorySQL(search.getCategory());
                    resultMap.put("list", serviceDao.selectAccountByCategory(mid, startDate, endDate, category));
                }
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
    public Map<String, Object> accountBookByYear(String mid, String mode, ReqServiceDto.AccountSearch search) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        String startDate = search.getYear() + "0101";
        String endDate = search.getYear() + "1231";
        switch ( mode ) {
            case "inout" :
                resultMap.put("list", serviceDao.selectAccountByParentCategory( mid, startDate, endDate, search.getOption().replaceAll(",", "")));
                break;
            case "title" :
                resultMap.put("list", serviceDao.selectAccountByTitle( mid, startDate, endDate, search.getTitle() ));
                break;
            case "inCategory":
            case "outCategory":
                if( search.getCategory() == null || search.getCategory().length == 0 ) {
                    resultMap.put("list", serviceDao.selectAllAccount( mid, startDate, endDate ));
                }else{
                    String category = makeCategorySQL( search.getCategory() );
                    resultMap.put("list", serviceDao.selectAccountByCategory( mid, startDate, endDate, category ));
                }
                break;
            case "period":
                resultMap.put("list", serviceDao.selectAllAccount( mid, search.getStart(), search.getEnd() ));
                break;
            case "all" :
            default:
                resultMap.put("list", serviceDao.selectAllAccount( mid, startDate, endDate ));
        }

        resultMap.put("inPrice", serviceDao.selectAccountPrice( mid, startDate, endDate, "01"));
        resultMap.put("outPrice", serviceDao.selectAccountPrice( mid, startDate, endDate, "02" ));
        return resultMap;
    }

    @Override
    public void deleteAccountBook(String mid, ReqServiceDto.DeleteAccount deleteAccount) throws Exception {
        String sql = makeDeleteSQL(deleteAccount.getId());
        serviceDao.deleteAccountBook(mid, sql);
    }


    //월 기준으로 그래프 조회
    private List<ResServiceDto.MonthChart> graphByMonth( String mid, ReqServiceDto.AccountSearch search ) throws Exception {
        List<String> makeDate = makeDate(search);
        String date = makeDate.get(0) + makeDate.get(1) + "01";

        return new ResServiceDto().toResMonthChart( serviceDao.selectGraphByMonth( mid, date ) );
    }

    //월 기준으로 json 얻기
    public JSONObject getJsonMonth( String mid, ReqServiceDto.AccountSearch search ) throws Exception {
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

    //년 기준으로 json 얻기
    @Override
    public JSONObject getJsonYear( String mid, ReqServiceDto.AccountSearch search ) throws Exception {
        List<ResServiceDto.YearChar> list = serviceDao.selectGraphByYear( mid , search);
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
        for( ResServiceDto.YearChar dto : list ) {
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


    public List<String> makeDate(ReqServiceDto.AccountSearch search) throws Exception {
        List<String> result = new ArrayList<>();


        if (search.getYear() == null && search.getMonth() == null) {
            localDate = LocalDate.now();

            result.add(String.valueOf(localDate.getYear()));
        } else {
            localDate = LocalDate.of(Integer.parseInt(search.getYear()), Integer.parseInt(search.getMonth()), 1);
            result.add(String.valueOf(localDate.getYear()));
        }

        if( localDate.getMonthValue() < 10 ){
            String month = String.valueOf(localDate.getMonthValue());
            result.add("0" + month);
        }else{
            result.add(String.valueOf(localDate.getMonthValue()));
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

    public String[] makeCategoryList(String[] category, int size) throws Exception {
        String[] result = new String[size];

        for (int i = 0; i < result.length; i++) {
            for (String s : category) {
                if (s.substring(3, 4).equals(i + 1 + "")) {
                    result[i] = s;
                }
            }
        }

        return result;
    }

    private String makeDeleteSQL(Long[] id) throws Exception {
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

    }
