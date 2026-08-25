package com.moneymanager.ledger.domain.entity;

import lombok.Getter;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * <p>
 * 패키지이름    : com.moneymanager.domain.ledger.entity<br>
 * 파일이름       : LedgerImage<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 17<br>
 * 설명              : LEDGER_IMAGE 테이블과 매칭되는 클래스
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
 * 		 	  <td>25. 12. 17.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class LedgerImage {
	private final Long id;										//이미지 식별번호
	private final Long ledgerId;							//가계부 시스템 고유번호(내부용)

	private final String imagePath;						//이미지 상대경로
	private final int sortOrder;							//나열 순서

	private final LocalDateTime createdAt;		//등록일
	private LocalDateTime updatedAt;		//수정일

	private LedgerImage(Long id, Long ledgerId, String imagePath, int sortOrder, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.ledgerId = ledgerId;
		this.imagePath = imagePath;
		this.sortOrder = sortOrder;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	//생성용
	public static LedgerImage of(Long ledgerId, Path path, int order) {
		return new LedgerImage(null, ledgerId, toImagePath(path),  order, LocalDateTime.now(), null);
	}

	//DB용
	public static LedgerImage restore(Long id, Long ledgerId, String imagePath, int sortOrder, LocalDateTime createdAt, LocalDateTime updatedAt) {
		return new LedgerImage(id, ledgerId, imagePath, sortOrder, createdAt, updatedAt);
	}


	//===== 유틸 메서드 =====
	private static String toImagePath(Path path) {
		return path.toString().replace("\\", "/");
	}
}