package com.areum.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
public class ReqMemberDto {

    //회원가입
    @Getter
    @Builder
    public static class Join {
        private String id;
        private String password;
        private String name;
        private String nickName;
        private String email;
        private String code;
        private Character gender;
    }

    //로그인
    @Getter
    @AllArgsConstructor
    public static class Login {
        private String id;
        private String pwd;
    }

    //아이디 찾기
    @Getter
    @AllArgsConstructor
    public static class FindId {
        private String name;
        private String email;
    }
}
