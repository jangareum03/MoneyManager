package com.moneymanager.domain.global.dto;

import lombok.Getter;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.global.dto<br>
 * 파일이름       : StoredFile<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 19<br>
 * 설명              :파일을 저장할 경로를 담기위한 클래스
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
 * 		 	  <td>26. 3. 19</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class StoredFile {
	private final String fullPath;	//실제 저장 경로
	private final String relativePath;	//DB 저장용

	public StoredFile(String fullPath, String relativePath) {
		this.fullPath = fullPath;
		this.relativePath = relativePath;
	}
}
