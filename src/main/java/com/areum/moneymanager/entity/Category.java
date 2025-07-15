package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Category {
    /* 카테고리번호(PK) */
    private String code;
    /* 부모카테고리 */
    private String parentCode;
    /* 카테고리이름 */
    private String name;
}
