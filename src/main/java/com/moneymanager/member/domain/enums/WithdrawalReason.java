package com.moneymanager.member.domain.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * <p>
 * 패키지이름    : com.moneymanager.member.domain.enums<br>
 * 파일이름       : WithdrawalReason<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 22<br>
 * 설명              : 회원 탈퇴 유형을 정의한 클래스
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
 * 		 	  <td>26. 9. 22</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public enum WithdrawalReason {

    NOT_USING_SERVICE("select.withdrawal.not-using"),
    CANNOT_ID_OR_NICKNAME("select.withdrawal.not-change"),
    PRIVACY_CONCERN("select.withdrawal.concern"),
    SERVICE_DISSATISFACTION("select.withdrawal.dissatisfaction"),
    OTHER("select.withdrawal.other");

    private final String messageKey;

    WithdrawalReason(String messageKey) {
        this.messageKey = messageKey;
    }

    public static WithdrawalReason from(String reason) {
        return Arrays.stream(values())
                .filter(r -> r.messageKey.equalsIgnoreCase(reason))
                .findFirst()
                .orElseThrow();
    }

}