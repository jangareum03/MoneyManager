package com.moneymanager.member.domain.entity;

import com.moneymanager.member.domain.enums.MemberGender;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.domain.enums<br>
 * 파일이름       : MemberInfo<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 11<br>
 * 설명              : MEMBER INFO 테이블과 매칭되는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 * 		<thead>
 * 		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 * 		 	  	<td>날짜</td>
 * 		 	  	<td>작성자</td>
 * 		 	  	<td>변경내용</td>
 * 		 	</tr>
 * 		</thead>
 * 		<tbody>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>26. 8. 11</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class MemberInfo {

	private final String id;							//회원번호(내부용)

	private MemberGender gender;			//성별
	private String profile;							//프로필
	private Long point;								//포인트
	private Long consecutiveDays;				//연속 출석일
	private Integer imageLimit;					//등록 가능한 가계부 이미지 개수
	private Integer failureCount;				//로그인 실패 횟수
	private LocalDateTime loginAt;				//마지막 접속일

	private MemberInfo(String id, MemberGender gender, String profile, Long point, Long consecutiveDays, Integer imageLimit, Integer failureCount, LocalDateTime loginAt) {
		this.id = id;
		this.gender = gender;
		this.profile = profile;
		this.point = point;
		this.consecutiveDays = consecutiveDays;
		this.imageLimit = imageLimit;
		this.failureCount = failureCount;
		this.loginAt = loginAt;
	}

	public static MemberInfo of(String id, MemberGender gender) {
		return new MemberInfo(id, gender, null, null, null, null, null, null);
	}

	public static MemberInfo restore(String id, MemberGender gender, String profile, Long point, Long consecutiveDays, Integer imageLimit, Integer failureCount, LocalDateTime loginAt) {
		return new MemberInfo(id, gender, profile, point, consecutiveDays, imageLimit, failureCount, loginAt);
	}

}