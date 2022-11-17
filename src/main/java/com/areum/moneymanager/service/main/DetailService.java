package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.ServiceDao;
import com.areum.moneymanager.dao.ServiceDaoImpl;
import com.areum.moneymanager.dto.ResServiceDto;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetailService {

    private final ServiceDao serviceDao;

    public DetailService(ServiceDaoImpl serviceDao ){
        this.serviceDao = serviceDao;
    }

    public JSONObject getJSONData(String mid) throws Exception {
        List<ResServiceDto.MonthChart> list = monthChartList(mid);

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
        for( ResServiceDto.MonthChart dto : list ) {
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

        System.out.println(data);
        return data;
    }


    //월로 차트 표시
    public List<ResServiceDto.MonthChart> monthChartList(String mid ) throws Exception {
        return new ResServiceDto().toResMonthChart(serviceDao.selectGraphByMonth( mid ));
    }

    //월로 전체조회
    public List<ResServiceDto.detailMonth> detailMonthList( String mid ) throws Exception {
        return serviceDao.selectAllAccountByMonth(mid);
    }

    //월 가격 총합
    public List<ResServiceDto.detailMonth> monthPrice( String mid ) throws Exception {
        return serviceDao.selectAllPriceByMonth(mid);
    }

}
