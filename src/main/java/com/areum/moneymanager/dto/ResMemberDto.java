package com.areum.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

}
