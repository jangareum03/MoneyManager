package com.areum.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.sql.Date;
import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class ResMemberDto {

    //아이디 찾기
    @Getter
    @AllArgsConstructor
    public static class FindId{
        private String id;
        private Timestamp date;
    }

    @Getter
    @AllArgsConstructor
    public static class FindPwd {
        private String email;
    }

    @Getter
    @Builder
    public static class Member {
        private String id;
        private char type;
        private String name;
        private String nickName;
        private char gender;
        private String profile;
        private Date joinDate;
        private int totalCount;
        private String email;
    }

}
