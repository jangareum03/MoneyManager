package com.moneymanager.domain.member.dto;

import com.moneymanager.domain.member.Member;
import com.moneymanager.domain.member.MemberInfo;
import com.moneymanager.domain.member.enums.MemberGender;
import com.moneymanager.domain.member.enums.MemberType;
import lombok.Builder;
import lombok.Getter;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.member.request<br>
 * * 파일이름       : MemberSignUpRequest<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 25.<br>
 * * 설명              : 회원가입 요청을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 25.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class MemberSignUpRequest {
	//아이디
	private String id;
	//비밀번호
	private String password;
	//이름
	private String name;
	//생년월일
	private String birth;
	//닉네임
	private String nickName;
	//이메일
	private String email;
	//성별
	private String gender;

	/**
	 * 사용자의 가입 요청 DTO을 {@link Member} 엔티티로 변환합니다.
	 * <p>클라이언트로부터 전달받은 회원 가입 정보를{@link Member} 와 {@link MemberInfo} 객체로 생성하여 매칭합니다. </p>
	 * <p>비밀번호는 암호화된 문자열을 매개변수로 전달받으며, {@code gender}는 문자열의 첫번째 문자를 추출하여 {@code char}로 변환됩니다.</p>
	 *
	 * @param encodedPassword		 암호화된 비밀번호
	 * @return	매핑된  {@link Member}객체
	 */
	public Member toEntity( String encodedPassword ) {
		MemberInfo memberInfo = MemberInfo.builder().gender(MemberGender.match(gender.charAt(0))).build();

		return Member.builder()
				.type(MemberType.NORMAL).role("ROLE_USER")
				.id(id)
				.password(encodedPassword)
				.name(name)
				.birthDate(birth)
				.nickName(nickName)
				.email(email)
				.detail(memberInfo)
				.build();
	}
}
