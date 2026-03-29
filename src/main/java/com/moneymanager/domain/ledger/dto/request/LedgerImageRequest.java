package com.moneymanager.domain.ledger.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.dto.request<br>
 * 파일이름       : LedgerImageRequest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 19<br>
 * 설명              : 가계부 이미지 요청을 위한 데이터 인터페이스
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
public interface LedgerImageRequest {
	boolean hasImage();
	List<MultipartFile> getImage();
}
