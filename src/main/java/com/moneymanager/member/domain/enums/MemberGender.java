package com.moneymanager.member.domain.enums;

import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import lombok.Getter;

import static com.moneymanager.global.exception.code.ErrorCode.INVALID_VALUE;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.domain.enums<br>
 * 파일이름       : MemberGender<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 11<br>
 * 설명              : 회원 성별을 정의한 클래스
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
public enum MemberGender {

	NORMAL("N"),
	MALE("M"),
	FEMALE("F");

	@Getter
	private final String value;		//DB 값

	MemberGender(String value) {
		this.value = value;
	}

	public static MemberGender fromValue(String value) {
		for (MemberGender gender : MemberGender.values()) {
			if (gender.value.equalsIgnoreCase(value)) {
				return gender;
			}
		}

		throw new ApplicationException(
				INVALID_VALUE,
				LogContent.of(
						"회원 성별 조회",
						MemberGender.class,
						value
				)
		);
	}

}