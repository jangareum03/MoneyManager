package com.areum.moneymanager.dto;

import com.areum.moneymanager.entity.AccountBook;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ResServiceDto {

    @Builder
    @Getter
    public static class MonthChart{
        private String category;
        private int price;
    }

    public List<MonthChart> toResMonthChart(List<AccountBook> accountBookList ){
        List<ResServiceDto.MonthChart> resultList = new ArrayList<>(accountBookList.size());
        for(AccountBook accountBook : accountBookList ) {
            resultList.add(
                    ResServiceDto.MonthChart.builder()
                            .category(accountBook.getCategory_id())
                            .price(accountBook.getPrice()).build()
            );
        }

        return resultList;
    }

    @Builder
    @Getter
    //내역조회(월) - 전체
    public static class detailMonth {
        private Long id;
        private String date;
        private String fix;
        private String code;
        private String name;
        private String title;
        private int price;
        private int totalPrice;
    }

}