package com.moneymanager.service.file;


import com.github.f4b6a3.ulid.UlidCreator;
import com.moneymanager.exception.BusinessException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import static com.moneymanager.exception.error.ErrorCode.*;
import static com.moneymanager.utils.validation.ValidationUtils.isNullOrBlank;


/**
 * <p>
 * 패키지이름    : com.moneymanager.service.file<br>
 * 파일이름       : LedgerImageNameStrategy<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 18<br>
 * 설명              : 가계부 파일 이름을 지정하는 클래스
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
 * 		 	  <td>26. 3. 18.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class LedgerImageNameStrategy implements FileNamingStrategy {

	@Override
	public String generateStoredName(String originalFileName) {
		if(isNullOrBlank(originalFileName)) {
			throw BusinessException.of(
					FILE_TARGET_MISSING,
					"파일 변경 실패   |   reason=형식오류   |   target=fileName   |   format=파일명.확장자   |   value=" + originalFileName
			);
		}

		//저장할 파일이름과 확장자
		String name = UlidCreator.getUlid().toString();
		String ext = getExtension(originalFileName);

		return  name+ '.' + ext;
	}

	//파일명에서 확장자 조회
	private String getExtension(String originalFileName) {
		int dotIndex = originalFileName.lastIndexOf(".");

		//점이 없거나, 맨 앞쪽에 있거나, 맨 끝에 있는 경우
		if(dotIndex <= 0 || dotIndex == (originalFileName.length() -1)) {
			throw BusinessException.of(
					FILE_TARGET_INVALID,
					"파일 변경 실패   |   reason=형식오류   |   target=fileName   |   format=파일명.확장자   |   value=" + originalFileName
			);
		}

		return originalFileName.substring(dotIndex+1).toLowerCase();
	}

	@Override
	public String generateRelativePath() {
		LocalDate today = LocalDate.now();

		String year = String.valueOf(today.getYear());
		String month = String.format("%02d", today.getMonthValue());

		return Path.of(year, month).toString();
	}

	@Override
	public String getBasePath() {
		return Paths.get("ledgers", "image").toString();
	}
}
