package com.areum.moneymanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.areum.moneymanager.dao.MemberDaoImpl;
import com.areum.moneymanager.entity.MemberInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;


@ExtendWith(MockitoExtension.class)
 class MemberServiceTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberDaoImpl memberDao;

    private MemberInfo createMember() {
        return MemberInfo.builder().member_id("UAt10001")
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
    void idCheckTest() {
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
    void nickNameCheckTest() {
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
    void makeMemberIdFirst() {
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
    void makeMemberIdSecond() {
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
    void makeMemberId(String mId, String returnValue, String id, String expected) {
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
    void makeMemberIdNo() {
        //given
        String id = "Orange";
        when(memberDao.selectMid("UAO10")).thenReturn("UAO10999");

        //when
        String mid = memberService.makeMemberId(id);

        //then
        assertEquals("에러", mid);
    }
}
