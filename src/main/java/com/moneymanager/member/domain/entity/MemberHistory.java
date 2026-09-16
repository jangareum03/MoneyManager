package com.moneymanager.member.domain.entity;

import com.moneymanager.member.domain.enums.HistoryType;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.domain.entity<br>
 * 파일이름       : MemberHistory<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 17<br>
 * 설명              : MEMBER_HISTORY 테이블과 매칭되는 클래스
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
 * 		 	  <td>26. 9. 17</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class MemberHistory {

    private Long id;
    private final String memberId;
    private final HistoryType type;
    private final String item;
    private final String beforeInfo;
    private final String afterInfo;
    private final LocalDateTime updatedAt;

    private MemberHistory(String memberId, HistoryType type, String item, String beforeInfo, String afterInfo, LocalDateTime updatedAt) {
        this.memberId = memberId;
        this.type = type;
        this.item = item;
        this.beforeInfo = beforeInfo;
        this.afterInfo = afterInfo;
        this.updatedAt = updatedAt;
    }

    public static MemberHistory create(String memberId, LocalDateTime updatedAt) {
        return new MemberHistory(memberId, HistoryType.CREATE, "회원가입", null, null, updatedAt);
    }

    public static  MemberHistory update(String memberId, String item, String beforeInfo, String afterInfo, LocalDateTime updatedAt) {
        return new MemberHistory(memberId, HistoryType.UPDATE, item, beforeInfo, afterInfo, updatedAt);
    }

    public static MemberHistory delete(String memberId, LocalDateTime updatedAt) {
        return new MemberHistory(memberId, HistoryType.DELETE, "회원탈퇴",  null, null, updatedAt);
    }

}