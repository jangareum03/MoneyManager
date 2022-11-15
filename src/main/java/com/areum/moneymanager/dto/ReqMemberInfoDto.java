package com.areum.moneymanager.dto;

import com.areum.moneymanager.entity.MemberInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReqMemberInfoDto {

    private ReqMemberInfoDto(){}

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

        public MemberInfo toEntity( String mid ) {
            return MemberInfo.builder().memberId(mid)
                    .id(id).password(password).name(name).nickName(nickName).email(email).gender(gender).build();
        }
    }

    //로그인
    @Getter
    @AllArgsConstructor
    public static class Login {
        private String id;
        private String pwd;

        public MemberInfo toEntity(){
            return MemberInfo.builder().id(id).password(pwd).build();
        }
    }

    //아이디 찾기
    @Getter
    @AllArgsConstructor
    public static class FindId {
        private String name;
        private String email;

        public MemberInfo toEntity() {
            return MemberInfo.builder().name(name).email(email).build();
        }
    }

    //비밀번호 찾기
    @Getter
    @AllArgsConstructor
    public static class FindPwd {
        private String name;
        private String id;

        public MemberInfo toEntity(){
            return MemberInfo.builder().name(name).id(id).build();
        }
    }

}
