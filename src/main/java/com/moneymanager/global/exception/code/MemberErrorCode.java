package com.moneymanager.global.exception.code;

/**
 * <p>
 * 패키지이름    : com.moneymanager.exception.code<br>
 * 파일이름       : MemberErrorCode<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 7<br>
 * 설명              : 회원 오류에 사용하는 에러코드
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
 * 		 	  <td>26. 7. 7</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public enum MemberErrorCode implements ErrorCode{

	NOT_FOUND_DATA("MBR-100", "존재하지 않은 회원입니다."),

	MBR_INVALID_CREDENTIALS("MBR-203", "아이디 또는 비밀번호를 다시 입력해주세요."),
	MBR_LIMIT_EXCEEDED("MBR-402", "로그인 횟수 초과로 로그인이 불가능합니다."),
	MBR_ACCOUNT_LOCKED("MBR-205", "로그인이 불가능한 계정입니다."),
	MBR_ACCOUNT_DISABLED("MBR-206", "비활성화된 계정으로 로그인이 불가능합니다."),
	MBR_ACCOUNT_DELETED("MBR-207", "탈퇴한 회원으로 로그인이 불가능합니다."),
	MBR_FORBIDDEN("MBR-300", "권한 부족으로 서비스를 이용할 수 없습니다.");

	private final String code;
	private final String defaultMessage;

	MemberErrorCode(String code, String defaultMessage) {
		this.code = code;
		this.defaultMessage = defaultMessage;
	}

	@Override
	public String getCode() {
		return code;
	}

}