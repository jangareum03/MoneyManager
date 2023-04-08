package com.areum.moneymanager.dto;

import com.areum.moneymanager.entity.LoginHistory;
import com.areum.moneymanager.entity.MemberInfo;
import com.areum.moneymanager.entity.UpdateHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;


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

    //사용자 인증
    @Builder
    @Getter
    public static class AuthMember implements UserDetails {
        private String id;
        private String password;
        private String role;

        public static AuthMember toDTO( MemberInfo memberInfo ) {
            return AuthMember.builder().id( memberInfo.getId() ).password( memberInfo.getPassword() ).role("ROLE_USER").build();
        }

        //권한 리턴
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            Collection<GrantedAuthority> collection = new ArrayList<>();
            collection.add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return getRole();
                }
            });
            return collection;
        }

        //비밀번호 리턴
        @Override
        public String getPassword() {
            return this.password;
        }

        //아이디 리턴
        @Override
        public String getUsername() {
            return this.id;
        }

        //계정만료 리턴
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        //계정잠금 리턴
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        //비밀번호 오래 사용 리턴
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        //계정 활성화 리턴
        @Override
        public boolean isEnabled() {
            return true;
        }
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
            return UpdateHistory.builder().memberId(mid).success('n').type('D').bfInfo("delete").afInfo("delete").deleteType(code).deleteCause(cause).build();
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

        public void encPassword( String password ) {
            this.password = password;
        }
    }

    //로그인 로그
    @Getter
    @AllArgsConstructor
    public static class LoginLog {
        private String id;
        private String cause;
        private char success;

        public LoginHistory toEntity( String ip, String browser ) {
            return LoginHistory.builder().login_id(id).browser(browser).ip(ip).success(success).cause(cause).build();
        }

    }

    //로그인
    @Getter
    @AllArgsConstructor
    public static class Login {
        private String id;
        private String password;

        public MemberInfo toEntity(){
            return MemberInfo.builder().id(id).password(password).build();
        }

        public MemberInfo toEntity( String mid ) {
            return MemberInfo.builder().memberId(mid).id(id).password(password).build();
        }

        public UpdateHistory toUpdateHistoryEntity( String mid ) {
            return UpdateHistory.builder().memberId(mid).success('n').type('D').bfInfo("recover").afInfo("recover").build();
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
