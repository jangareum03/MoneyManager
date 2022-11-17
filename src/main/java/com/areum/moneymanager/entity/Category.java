package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Category {
    private String name;
    private String code;
    private String parent_code;
}
