package com.areum.moneymanager.service.member;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.areum.moneymanager.dao.MemberInfoDaoImpl;
import com.areum.moneymanager.dto.ReqMemberInfoDto;
import com.areum.moneymanager.entity.MemberInfo;
import com.areum.moneymanager.service.member.MemberServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.stream.Stream;


@ExtendWith(MockitoExtension.class)
 class MemberServiceTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberInfoDaoImpl memberDao;

    private MemberInfo createMember() {
        return MemberInfo.builder().memberId("UAt10001")
                .id("test01")
                .password("test01!!")
                .name("테스트")
                .nickName("테스트닉네임")
                .gender('1')
                .email("test@naver.com")
                .point(0)
                .build();
    }

    @DisplayName("중복된 아이디 확인")
    @Test
    void idCheckTest() throws SQLException {
        //given
        String id = "test01";
        when(memberDao.selectCountById(id)).thenReturn(1);
        when(memberDao.selectCountById("test02")).thenReturn(0);

        //when
        int count1 = memberService.idCheck(id);
        int count2 = memberService.idCheck("test02");

        //then
        assertEquals(1, count1);
        assertEquals(0, count2);
    }

    @DisplayName("닉네임 중복 확인")
    @Test
    void nickNameCheckTest() throws SQLException {
        //given
        String nickName = "테스트";
        when(memberDao.selectCountByNickName(nickName)).thenReturn(1);
        when(memberDao.selectCountByNickName("바부야")).thenReturn(0);

        //when
        int count1 = memberService.nickNameCheck(nickName);
        int count2 = memberService.nickNameCheck("바부야");

        //then
        assertEquals(1, count1);
        assertEquals(0, count2);
    }

    @DisplayName("회원번호 처음생성")
    @Test
    void makeMemberIdFirst() throws SQLException {
        //given
        String id = "test01";
        when(memberDao.selectMid("UAt10")).thenReturn(null);

        //when
        String mid = memberService.makeMemberId(id);

        //then
        assertEquals("UAt10001", mid);
    }

    @DisplayName("회원번호 두번째 생성")
    @Test
    void makeMemberIdSecond() throws SQLException {
        //given
        String id = "apple";
        when(memberDao.selectMid("UAa10")).thenReturn("UAa10001");

        //when
        String mid = memberService.makeMemberId(id);

        //then
        assertEquals("UAa10002", mid);
    }

    @DisplayName("회원번호 경계값 생성")
    @ParameterizedTest
    @MethodSource("mmiParam")
    void makeMemberId(String mId, String returnValue, String id, String expected) throws SQLException {
        //given
        when(memberDao.selectMid(mId)).thenReturn(returnValue);

        //when
        String mid = memberService.makeMemberId(id);

        //then
        assertEquals(expected, mid);
    }
    private static Stream<Arguments> mmiParam() {
        return Stream.of(
                Arguments.of("UAw10", "UAw10009", "wow", "UAw10010"),
                Arguments.of("UAw10", "UAw10099", "wow12", "UAw10100"),
                Arguments.of("UAw10", "UAw10599", "wwe", "UAw10600")
        );
    }

    @DisplayName("회원번호 생성불가")
    @Test
    void makeMemberIdNo() throws SQLException {
        //given
        String id = "Orange";
        when(memberDao.selectMid("UAO10")).thenReturn("UAO10999");

        //when
        String mid = memberService.makeMemberId(id);

        //then
        assertEquals("에러", mid);
    }

    @Test
    @DisplayName("성공 - 특정회원의 비밀번호 찾기")
    void loginSuccess() throws SQLException {
        //given
        when(memberDao.selectPwd("test01")).thenReturn("test01!!");
        //when
        int result = memberService.loginCheck(new ReqMemberInfoDto.Login("test01", "test01!!"));
        //then
        assertEquals(1, result);
    }

    @Test
    @DisplayName("실패 - 특정회원 비밀번호 다름")
    void loginPwd() throws SQLException {
        //given
        when(memberDao.selectPwd("test01")).thenReturn("test01@@");
        //when
        int result = memberService.loginCheck(new ReqMemberInfoDto.Login("test01", "test01!!"));
        //then
        assertEquals(0, result);
    }

    @Test
    @DisplayName("실패 - 회원 아이디 없음")
    void loginFail() throws SQLException {
        //given
        when(memberDao.selectPwd("test01")).thenReturn("");
        //when
        int result = memberService.loginCheck(new ReqMemberInfoDto.Login("test01", "test01!!"));
        //then
        assertEquals(-1, result);
    }

}
