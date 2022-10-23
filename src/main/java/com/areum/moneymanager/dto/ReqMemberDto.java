package com.areum.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ReqMemberDto {

    @Getter
    @AllArgsConstructor
    public static class Join {
        private String id;
        private String password;
        private String name;
        private String nickName;
        private String email;
        private Character gender;
    }
}
