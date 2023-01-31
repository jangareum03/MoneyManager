package com.areum.moneymanager.dto;

import com.areum.moneymanager.entity.AccountBook;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ResServiceDto {

    //카테고리
    @Builder
    @Getter
    public static class Category {
        private String name;
        private String code;

        public static List<ResServiceDto.Category> entityToDto(List<com.areum.moneymanager.entity.Category> entityList) {
            List<ResServiceDto.Category> resultList = new ArrayList<>();

            for( com.areum.moneymanager.entity.Category entity : entityList ) {
                resultList.add( Category.builder().name(entity.getName()).code(entity.getCode()).build() );
            }

            return resultList;
        }
    }

    //가계부 정보
    @Builder
    @Getter
    public static class DetailAccount {
        private Long id;
        private String date;
        private String fix;
        private String fixOption;
        private String category;
        private String title;
        private String content;
        private int price;
        private String priceType;
        private String image;
        private String mapName;
        private String mapRoad;


        public static DetailAccount toDto( AccountBook entity ) {
            return DetailAccount.builder().id(entity.getId()).date(entity.getAccount_date()).fix(entity.getFix()).category(entity.getCategory_id()).fixOption(entity.getFix_option())
                    .title(entity.getTitle()).content(entity.getContent()).price(entity.getPrice()).priceType(entity.getPrice_type()).image(entity.getImage1()).mapName(entity.getLocation_name()).mapRoad(entity.getLocation()).build();
        }
    }

    //자주묻는질문
    @Builder
    @Getter
    public static class Faq {
        private String question;
        private String answer;
    }

    @Builder
    @Getter
    //리스트에서 전체 내역
    public static class ListAccount {
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

    //주 차트
    @Builder
    @Getter
    public static class WeekChart{
        private int week;
        private int inPrice;
        private int outPrice;
    }

    //년 차트
    @Builder
    @Getter
    public static class YearChart{
        private String month;
        private int inPrice;
        private int outPrice;
    }

}
