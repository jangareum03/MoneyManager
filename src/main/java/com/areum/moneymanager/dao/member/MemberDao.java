package com.areum.moneymanager.dao.member;

import com.areum.moneymanager.entity.Member;




/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dao.member<br>
 *  * 파일이름       : MemberDao<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원 데이터를 정의한 인터페이스
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
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>클래스 전체 리팩토링(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
public interface MemberDao {

	boolean saveMember( Member member );

	Member findMemberById( String id );

	Member findAuthMemberByUsername( String username );

	Member findLoginMemberByUsername(String username );

	Member findStatusByUsername( String username );

	Member findEmailAndNameByMemberId( String username );

	String findUsernameByMemberId( String memberId );

	String findUsernameByNameAndBirth( Member member );

	String findEmailByIdAndName( Member member );

	String findPasswordByUsername( String username );

	String findMaxMemberIdByLike( String memberId );

	int countByUsernameEquals( String username );

	int countByNickNameEquals( String nickName );

	int countByEmailEquals( String email );

	void updateStatus( Member member );

	boolean updatePassword( String username, String password );

	boolean updateName(String username, String name );

	boolean updateEmail(String username, String email );

	boolean updateResignDate( String username );

}
