package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class MemberInfo {
    private String member_id;
    private String id;
    private String password;
    private String name;
    private String nickName;
    private char gender;
    private String email;
    private String profile;
    private int point;
}
