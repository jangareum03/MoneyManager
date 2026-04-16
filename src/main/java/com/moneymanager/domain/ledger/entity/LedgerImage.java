package com.moneymanager.domain.ledger.entity;

import com.moneymanager.exception.BusinessException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.moneymanager.exception.error.ErrorCode.*;
import static com.moneymanager.utils.validation.ValidationUtils.isNullOrBlank;

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
@Builder
@Getter
public class LedgerImage {
	private Long id;										//이미지 식별번호
	private Long ledgerId;							//가계부 시스템 고유번호(내부용)

	private String imagePath;						//이미지 상대경로
	private int sortOrder;							//나열 순서

	private LocalDateTime createdAt;		//등록일
	private LocalDateTime updatedAt;		//수정일

	/**
	 *	가계부 이미지 정보를 검증하고, {@link LedgerImage} 객체를 생성합니다.
	 *<p>
	 *     검증할 항목:
	 *     <ul>
	 *         <li>가계부 ID 유효성</li>
	 *         <li>이미지 경로 유효성</li>
	 *         <li>정렬 순서 값 유효성</li>
	 *     </ul>
	 *</p>
	 * 검증 실패 시 {@link BusinessException}가 발생합니다.
	 *
	 * @param ledgerId	가계부 ID
	 * @param path	이미지 저장한 상대경로
	 * @param order 이미지 정렬 순서
	 * @return	검증된 정보를 기반으로 생성된 {@link LedgerImage} 객체
	 * @throws BusinessException	입력값이 유효하지 않은 경우 발생
	 */
	public static LedgerImage create(Long ledgerId, String path, int order) {
		String dbPath = path.replace("\\", "/");

		if(!dbPath.startsWith("/")) {
			dbPath = "/" + dbPath;
		}

		validateLedgerId(ledgerId);
		validateOrder(order);
		validatePath(dbPath);

		return LedgerImage.builder()
				.ledgerId(ledgerId)
				.imagePath(dbPath)
				.sortOrder(order)
				.build();
	}

	private static void validateLedgerId(Long id) {
		if(id == null) {
			throw BusinessException.of(
					LEDGER_TARGET_MISSING,
					"가계부 이미지 검증 실패   |   reason=필수값누락   |   object=LedgerImage   |   field=ledgerId   |   value=" + id
			);
		}

		if(id < 1) {
			throw BusinessException.of(
					LEDGER_TARGET_RANGE,
					"가계부 이미지 검증 실패   |   reason=범위오류   |   object=LedgerImage   |   field=ledgerId   |   min=1   |   value=" + id
			);
		}
	}

	private static void validatePath(String path) {
		if(isNullOrBlank(path)) {
			throw BusinessException.of(
					LEDGER_TARGET_MISSING,
					"가계부 이미지 검증 실패   |   reason=필수값누락   |   object=LedgerImage   |   field=imagePath   |   value=" + path
			);
		}

		if(path.contains("\\")) {
			throw BusinessException.of(
					LEDGER_TARGET_FORMAT,
					"가계부 이미지 검증 실패   |   reason=형식오류   |   object=LedgerImage   |   field=imagePath   |   expectedFormat=연도/월/이미지명.확장자   |   value=" + path
			);
		}
	}

	private static void validateOrder(int order) {
		if(!(0 < order && order < 4)) {
			throw BusinessException.of(
					LEDGER_TARGET_RANGE,
					"가계부 이미지 검증 실패   |   reason=범위오류   |   object=LedgerImage   |   field=sortOrder   |   min=1   |   max=3   |   value=" + order
			);
		}
	}
}
