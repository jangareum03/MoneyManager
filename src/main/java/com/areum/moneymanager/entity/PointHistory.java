package com.areum.moneymanager.entity;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Builder
@Getter
public class PointHistory {
	/* 사용내역 번호(PK) */
	private Long id;
	/* 회원번호(FK: member_id) */
	private Member member;
	/* 포인트 유형 */
	private String type;
	/* 포인트 값 */
	private Long points;
	/* 변경이유 */
	private String reason;
	/* 잔여 포인트 */
	private Long balancePoints;
	/* 사용된 날짜 */
	private Timestamp usedAt;
}
