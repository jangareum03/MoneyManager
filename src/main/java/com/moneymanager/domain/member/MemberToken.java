package com.moneymanager.domain.member;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * <p>
 * 패키지이름    : com.moneymanager.entity<br>
 * 파일이름       : MemberToken<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 11.<br>
 * 설명              : TB_MEMBER_TOKEN 테이블과 매칭되는 엔티티 클래스
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
 * 		 	  <td>25. 11. 11.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class MemberToken {
	/* 회원번호(PK) */
	private Member member;
	/* 엑세스 토큰 */
	private String accessToken;
	/* 리프레시 토큰 */
	private String refreshToken;
	/* 엑세스 만료일시 */
	private LocalDateTime accessExpireAt;
	/* 리프레시 만료일시 */
	private LocalDateTime refreshExpireAt;
	/* 마지막 발급일시 */
	private LocalDateTime lastExpireAt;
	/* 생성일시 */
	private LocalDateTime createdAt;
	/* 수정일시 */
	private LocalDateTime updatedAt;
}
