package com.areum.moneymanager.dao.member;

import com.areum.moneymanager.entity.MemberInfo;

import java.sql.Timestamp;



/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.dao.member<br>
 *  * 파일이름       : MemberInfoDao<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 회원상세 데이터를 정의한 인터페이스
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
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
public interface MemberInfoDao extends MemberDao {

	boolean saveMemberInfo( MemberInfo memberInfo );

	MemberInfo findMemberInfoById( String id );

	Timestamp findLastLoginDateByUserName( String username );

	Long findPointByMemberId( String memberId );

	String findProfileImageNameById( String id );

	boolean updateGender( String username, char gender );

	boolean updateProfile( String id, String imageName );

	void updateLastLoginDate( MemberInfo memberInfo );

	Long updatePointAndReturn( String memberId, int point );


}
