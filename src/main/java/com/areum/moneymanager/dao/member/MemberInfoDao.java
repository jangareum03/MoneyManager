package com.areum.moneymanager.dao.member;

import com.areum.moneymanager.entity.MemberInfo;

import java.sql.Timestamp;

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
