package com.areum.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
public class ReqMemberDto {

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
}
