package com.areum.moneymanager.dto;

import com.areum.moneymanager.entity.AccountBook;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ResAccountBookDto {

    public ResAccountBookDto() {}

    @Builder
    @Getter
    public static class MonthChart{
        private String category;
        private int price;
    }

    public List<ResAccountBookDto.MonthChart> toResMonthChart(List<AccountBook> accountBookList ){
        List<ResAccountBookDto.MonthChart> resultList = new ArrayList<>(accountBookList.size());
        for(AccountBook accountBook : accountBookList ) {
            resultList.add(
                    ResAccountBookDto.MonthChart.builder()
                            .category(accountBook.getCategory_id())
                            .price(accountBook.getPrice()).build()
            );
        }

        return resultList;
    }

}
