package com.moneymanager.enums;

import lombok.Getter;

/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.enums<br>
 *  * 파일이름       : RegexPattern<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 정규식 패턴을 정의한 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>22. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Getter
public enum RegexPattern {


	/**
	* 회원 관련된 정규식 패턴 모음<br><br>
	* <ul>
	 *     <li>아이디(MEMBER_ID)	: 4~15자 사이의 영어/숫자만 입력 가능, 숫자로 시작 불가</li>
	 *     <li>비밀번호(MEMBER_PWD)	:	8~20자 사이의 영어/숫자/특수문자(!%#^*)만 입력 가능</li>
	 *     <li>이름(MEMBER_NAME)	:	2~5자 사이의 한글만 입력 가능</li>
	 *     <li>닉네임(MEMBER_NICKNAME)	:	2~10자 사이의 영어/숫자/한글만 입력 가능</li>
	 *     <li>이메일(MEMBER_EMAIL)	:	'로컬파트@도메인파트' 형식으로 입력가능, 로컬파트는 영어/숫자/특수문자(!#$%&`*+-/=?^_'{|}~)만 입력 가능, 도메인파트는 영어/숫자/특수문자(-)만 입력 가능하며  점으로 시작 불가, 이메일 전체에서 연속된 점 불가</li>
	 *     <li>이메일코드(MEMBER_EMAIL_CODE)	: 6글자의 영어/숫자만 입력 가능</li>
	* </ul>
	*/
	MEMBER_ID("^[a-zA-Z][a-zA-Z0-9]{3,14}$"),
	MEMBER_PWD("^(?=.*[a-zA-Z]){0,}(?=.*[0-9]){0,}(?=.*[!%#^*]){0,}(?!(?:^[a-zA-Z]+$|^[0-9]+$|^[!%#^*]+$))[a-zA-Z0-9!%#^*]{8,20}$"),
	MEMBER_NAME("^[가-힣]{2,5}"),
	MEMBER_BIRTH("^(1|2)[0-9]{3}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])$"),
	MEMBER_NICKNAME("^[a-zA-Z0-9가-힣]{2,10}"),
	MEMBER_EMAIL("^(?!.*\\.\\.)[A-Za-z0-9!#$%&`*+-/=?^_'{|}~]+@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)+$"),
	MEMBER_EMAIL_CODE("^[A-Za-z0-9]{6}$"),

	/**
	 * 가계부 관련된 정규식 패턴 모음<br>
	 *
	 * <ul>
	 *     <li>년도(BUDGET_YEAR)	: 4자로 숫자만 입력 가능, 0으로 시작 불가</li>
	 *     <li>월(BUDGET_MONTH)	: 2자로 숫자만 입력가능</li>
	 *     <li>주(BUDGET_WEEK)		: 2자로 숫자만 입력가능</li>
	 *     <li>일(BUDGET_DAY)			: 2자로 숫자만 입력가능</li>
	 * </ul>
	 *
	 *
	 *
	 *
	 */
	BUDGET_YEAR("^[1-9][\\d]{3}"),
	BUDGET_MONTH("^(1[0-2]|[1-9])$"),
	BUDGET_WEEK("^[1-5]$"),
	BUDGET_DAY("^[0-3][\\d]{1}"),
	BUDGET_IMAGE("[^a-zA-Z0-9가-힣_\\-\\.]"),


	/**
	 * Q&A 관련된 정규식 패턴 모음
	 * <ul>
	 *     <li>제목(QUESTION_TITLE)			: 20글자 미만 입력 가능</li>
	 *     <li>내용(QUESTION_CONTENT)		: 300글자 미만 입력 가능, 띄어쓰기로 시작 불가</li>
	 * </ul>
	 */
	QUESTION_TITLE("^[a-zA-Z0-9가-힣].{0,19}$"),
	QUESTION_CONTENT("^(?!\\s)[\\s\\S]{1,299}$");
	private final String pattern;

	RegexPattern( String pattern ) {
		this.pattern = pattern;
	}

}
