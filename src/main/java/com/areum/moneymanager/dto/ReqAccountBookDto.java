package com.areum.moneymanager.dto;

import com.areum.moneymanager.entity.AccountBook;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public class ReqAccountBookDto {

    private ReqAccountBookDto() {}

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

    @Builder
    @Getter
    public static class MonthChart{
        private String category;
        private String accountDate;

        public AccountBook toEntity(){
            return AccountBook.builder()
                    .category_id(category)
                    .account_date(accountDate)
                    .build();
        }
    }
}
