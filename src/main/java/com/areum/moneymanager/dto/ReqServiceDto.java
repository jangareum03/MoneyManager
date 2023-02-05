package com.areum.moneymanager.dto;

import com.areum.moneymanager.entity.AccountBook;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ReqServiceDto {

    //날짜 이동
    @Getter
    @Builder
    public static class MoveDate {
        private int year;
        private int month;
    }

    //가계부 구분
    @Builder
    @Getter
    public static class DivisionAccount {
        private String code;
        private String year;
        private String month;
        private String date;
    }

    //가계부 작성
    @Builder
    @Getter
    public static class Write {
        private String fix;
        private String fixOption;
        private String category;
        private String accountDate;
        private String title;
        private String content;
        private int price;
        private String priceType;
        private List<MultipartFile> images;
        private String mapName;
        private String mapRoad;
        private String mapAddr;

        public AccountBook toEntity() {
            return AccountBook.builder()
                    .fix(fix).fix_option(fixOption)
                    .category_id(category).account_date(accountDate)
                    .title(title).content(content)
                    .price(price).price_type(priceType)
                    .image1(String.valueOf(images.get(0).getOriginalFilename()))
                    .image2(String.valueOf(images.get(1).getOriginalFilename()))
                    .image3(String.valueOf(images.get(2).getOriginalFilename()))
                    .location_name(mapName).location(mapRoad).build();
        }
    }

    //가계부 검색
    @Builder
    @Getter
    public  static class AccountSearch{
        private String year;
        private String month;
        private String week;
        private String option;
        private String title;
        private String start;
        private String end;
        private String basicInCategory;
        private String basicExCategory;
        private String[] inCategory;
        private String[] exCategory;
    }

    //가계부 수정
    @Builder
    @Getter
    public static class UpdateAccount {
        private Long id;
        private String fix;
        private String fixOption;
        private String category;
        private String accountDate;
        private String title;
        private String content;
        private int price;
        private String priceType;
        private List<MultipartFile> images;
        private String mapName;
        private String mapRoad;

        public AccountBook toEntity() {
            return AccountBook.builder().id(id)
                    .fix(fix).fix_option(fixOption).category_id(category).account_date(accountDate)
                    .title(title).content(content).price(price).price_type(priceType)
                    .image1(String.valueOf(images.get(0).getOriginalFilename()))
                    .location_name(mapName).location(mapRoad).build();
        }
    }

    //가계부 삭제
    @Builder
    @Getter
    public static class DeleteAccount{
        private Long[] id;
    }

    //카테고리 얻기
    @Builder
    @Getter
    public static class GetCategory{
        private String code;
    }

    //공지사항 리스트 얻기
    @Builder
    @Getter
    public static class NoticeList{
        private int start;
        private int end;
    }
}
