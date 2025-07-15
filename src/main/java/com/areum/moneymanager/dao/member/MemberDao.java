package com.areum.moneymanager.dao.member;

import com.areum.moneymanager.entity.Member;


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
