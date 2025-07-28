package com.areum.moneymanager.dto.common.response;

import lombok.Builder;
import lombok.Getter;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.common.response<br>
 * * 파일이름       : PageResDTO<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 26.<br>
 * * 설명              : 페이지네이션 응답을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 26.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class PageResDTO {
	//이전 페이지 표시여부
	private boolean isPrev;
	//다음 페이지 표시여부
	private boolean isNext;
	//현재 페이지 번호
	private Integer page;
	//시작 페이지 번호
	private Integer startPage;
	//끝 페이지 번호
	private Integer endPage;
	//한 페이지 당 보여질 게시물 수
	private Integer size;
}
