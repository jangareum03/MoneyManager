package com.moneymanager.global.domain.dto.response.api;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.domain.dto.response<br>
 * 파일이름       : ErrorBody<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 24<br>
 * 설명              : API 실패 응답 Body 정보를 담은 클래스
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
 * 		 	  <td>26. 8. 24</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class ErrorBody {

    @Getter
    private final String message;

    private ErrorBody(String message) {
        this.message = message;
    }

    public static ErrorBody of(String message) {
        return  new ErrorBody(message);
    }

}