package com.areum.moneymanager.dto;

import com.areum.moneymanager.entity.MemberInfo;
import com.areum.moneymanager.entity.UpdateHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;


@Getter
public class ReqMemberDto {

    private ReqMemberDto(){}

    //출석체크 확인
    @Getter
    @Builder
    public static class AttendCheck{
        private String mid;
        private String startDate;
        private String endDate;
    }

    //회원정보 삭제
    @Getter
    @Builder
    public static class Delete {
        private String id;
        private String password;
        private String code;
        @Nullable
        private String cause;

        public MemberInfo toEntity( String mid ) {
            return MemberInfo.builder().memberId(mid).id(id).password(password).build();
        }

        public UpdateHistory toUpdateHistoryEntity( String mid ) {
            return UpdateHistory.builder().memberId(mid).type('D').bfInfo("delete").afInfo("delete").deleteType(code).deleteCause(cause).build();
        }
    }

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


    //회원정보 수정
    @Getter
    @AllArgsConstructor
    public static class Update {
        @Nullable
        private String name;
        @Nullable
        private String password;
        @Nullable
        private char gender;
        @Nullable
        private String email;
        @Nullable
        private MultipartFile profile;

        public MemberInfo toEntity() {
            if( profile == null ) {
                return MemberInfo.builder().name(name).password(password).gender(gender).email(email).build();
            }else{
                return MemberInfo.builder().name(name).password(password).gender(gender).email(email).profile(profile.getOriginalFilename()).build();
            }
        }

    }

}
