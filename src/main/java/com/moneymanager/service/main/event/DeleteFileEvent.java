package com.moneymanager.service.main.event;

import lombok.Getter;

import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.main.event<br>
 * 파일이름       : DeleteFileEvent<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 23<br>
 * 설명              : 파일 삭제 시 필요한 이벤트 클래스
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
 * 		 	  <td>25. 12. 23</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class DeleteFileEvent {

	private final List<String> paths;

	public DeleteFileEvent(List<String> paths) {
		this.paths = paths;
	}

}