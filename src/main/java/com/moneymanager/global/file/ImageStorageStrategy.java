package com.moneymanager.global.file;

import com.github.f4b6a3.ulid.UlidCreator;
import com.moneymanager.delete.domain.global.dto.StoredFile;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.log.DeveloperLogInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.time.Clock;

import static com.moneymanager.global.exception.code.CommonErrorCode.INVALID_FORMAT;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.file<br>
 * 파일이름       : ImageStorageStrategy<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 5. 27<br>
 * 설명              : 이미지 파일을 저장하기 위한 규칙을 지정하는 추상 클래스
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
 * 		 	  <td>26. 5. 27</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@RequiredArgsConstructor
public abstract class ImageStorageStrategy<T> implements FileStorageStrategy<T> {

	protected final Clock clock;
	@Getter
	protected final String rootPath;

	@Override
	public StoredFile createStoredFile(T t, String fileName) {
		//1. 서버에 저장될 파일명
		String ext = getExtension(fileName);
		String storedName = generateFileName() + "." + ext;

		//2. 경로
		String directoryPath = generateRelativePath(t);
		String relativePath = String.valueOf(Path.of(directoryPath, storedName));
		Path fullPath = Path.of(getRootPath(), relativePath);

		return new StoredFile(fullPath, relativePath);
	}

	protected String generateFileName() {
		return UlidCreator.getUlid().toString();
	}

	protected String getExtension(String file) {
		int dotIndex = file.lastIndexOf(".");

		//점 없음, 맨 앞쪽에 있음, 맨 끝에 있는 경우
		if(dotIndex <= 0 || dotIndex == (file.length() - 1)) {
			throw ValidationException.of(
					INVALID_FORMAT,
					DeveloperLogInfo.of("이미지 검증", "확장자 형식 불일치", "file", file),
					"파일 형식이 올바르지 않습니다."
			);
		}

		return file.substring(dotIndex+1).toLowerCase();
	}

	protected abstract String generateRelativePath(T t);

	protected abstract Path generateAbsolutePath(String relativePath);
}