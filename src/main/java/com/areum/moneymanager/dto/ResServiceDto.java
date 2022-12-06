package com.areum.moneymanager.dto;

import com.areum.moneymanager.entity.AccountBook;
import com.areum.moneymanager.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ResServiceDto {

    //수입 카테고리
    @Builder
    @Getter
    public static class Category {
        private String name;
        private String code;

        public static List<Category> toResIncomeCategory(List<com.areum.moneymanager.entity.Category> categoryList ){
            List<Category> resultList = new ArrayList<>(categoryList.size());

            for( com.areum.moneymanager.entity.Category category : categoryList )  {
                resultList.add( categoryEntityToDto(category) );
            }

            return resultList;
        }


        private static Category categoryEntityToDto(com.areum.moneymanager.entity.Category category ) {
            if( category == null ) {
                return null;
            }

            return ResServiceDto.Category.builder().name(category.getName()).code(category.getCode()).build();
        }
    }

    @Builder
    @Getter
    //내역조회(월) - 전체
    public static class DetailList {
        private Long id;
        private String date;
        private String fix;
        private String code;
        private String name;
        private String title;
        private int price;
        private int totalPrice;
    }

    //월 차트
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

    //년 차트
    @Builder
    @Getter
    public static class YearChart{
        private String month;
        private int inPrice;
        private int outPrice;
    }

    @Builder
    @Getter
    public static class WeekChart{
        private int week;
        private int inPrice;
        private int outPrice;
    }


}
