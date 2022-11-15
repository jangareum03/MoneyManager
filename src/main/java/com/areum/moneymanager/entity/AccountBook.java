package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

import java.sql.Date;

@Builder
@Getter
public class AccountBook {
    private Long id;                                //가계부 번호(PK)
    private String member_id;               //작성자(FK)
    private String category_id;             //카테고리 번호(FK)
    private String fix;                             //고정여부
    private String fix_option;              // 고정옵션
    private String account_date;        //가계부 날짜
    private String title;                           //제목
    private String content;                     //내용
    private int price;                                  //가격
    private String price_type;            //가격옵션
    private String image1;                      //이미지
    private String image2;                      //이미지
    private String image3;                      //이미지
    private String location_name;       //장소명
    private String location;                    //장소
    private Date regdate;                       //등록일
    private Date modified_date;             //수정일
}
