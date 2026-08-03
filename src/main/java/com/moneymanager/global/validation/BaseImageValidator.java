package com.moneymanager.global.validation;

import com.moneymanager.global.exception.exception.BusinessException;
import com.moneymanager.global.exception.exception.ExternalException;
import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.log.DeveloperLogInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static com.moneymanager.global.exception.code.CommonErrorCode.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.validation<br>
 * 파일이름       : BaseImageValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 18<br>
 * 설명              : 기본적인 이미지 파일을 검증하는 추상 클래스
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
public abstract class BaseImageValidator implements ImageValidator {

	private final String work = "파일 검증";

	protected void checkIsImage(String contentType) {
		if(contentType == null || !contentType.startsWith("image/")) {
			throw BusinessException.of(
					UNSUPPORTED_FILE_TYPE,
					DeveloperLogInfo.of(work, "이미지 파일 아님", MultipartFile.class, "contentType", contentType)
							.addOption("policy", "이미지가 아닌 다른 파일 업로드 시도"),
					"이미지 파일만 업로드 할 수 있습니다."
			);
		}
	}

	protected void checkExtension(String fileName, List<String> allowedExtensions) {
		if(fileName == null || !fileName.contains(".")) {
			throw ValidationException.of(
					INVALID_FORMAT,
					DeveloperLogInfo.of(work,"파일 확장자 없음", MultipartFile.class, "fileName", fileName),
					"잘못된 파일 이름입니다. 다른 파일로 진행해주세요."
			);
		}

		String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

		if(!allowedExtensions.contains(ext)) {
			throw BusinessException.of(
					UNSUPPORTED_FILE_TYPE,
					DeveloperLogInfo.of(work, "허용되지 않은 확장자", MultipartFile.class, "fileName", fileName)
							.addOption("allowed", allowedExtensions),
					"지원하지 않은 확장자입니다. 다른 파일로 진행해주세요."
			);
		}
	}

	protected void checkHeader(MultipartFile file, List<String> allowedHeaders) {
		try(InputStream is = file.getInputStream()){
			byte[] header = new byte[8];
			int read = is.read(header);

			if(read < 4) {
				throw BusinessException.of(
						FILE_CORRUPTED,
						DeveloperLogInfo.of(work, "파일 헤더 길이 부족", MultipartFile.class, "header", String.valueOf(read))
								.addOption("size", 4),
						"손상된 파일입니다. 다른 파일로 진행해주세요."
				);
			}

			String hex = byteToHex(Arrays.copyOf(header, 4));

			if(!allowedHeaders.contains(hex)) {
				throw BusinessException.of(
						UNSUPPORTED_FILE_TYPE,
						DeveloperLogInfo.of(work, "허용하지 않은 파일", MultipartFile.class, "headerHex", hex)
								.addOption("allowed", allowedHeaders.toString()),
						"지원하지 않은 파일입니다. 다른 파일로 진행해주세요."
				);
			}
		}catch (IOException e) {
			throw ExternalException.of(
					FILE_READ_FAILED,
					DeveloperLogInfo.of(work, "파일 읽기 불가", MultipartFile.class, "fileName", file.getOriginalFilename()),
					"읽을 수 없는 파일입니다. 다른 파일로 진행해주세요.",
					e
			);
		}
	}

	//byte를 16진수로 변환
	private String byteToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();

		for(byte b : bytes) {
			sb.append(String.format("%02X", b));
		}

		return sb.toString();
	}

	protected void checkSize(long size) {
		long max = 5 * 1024 * 1024;

		if(size <= 0) {
			throw ExternalException.of(
					FILE_READ_FAILED,
					DeveloperLogInfo.of(work, "파일 사이즈 0", MultipartFile.class, "size", String.valueOf(size))
									.addOption("min", 1),
					"빈 파일은 업로드 할 수 없습니다."
			);
		}

		if(size > max) {
			throw ExternalException.of(
					FILE_TOO_LARGE,
					DeveloperLogInfo.of(work, "파일 용량 초과", MultipartFile.class, "size", String.valueOf(size))
							.addOption("max", max),
					String.format("파일은 최대 %dMB까지만 업로드 가능합니다. 파일을 확인해주세요.", max)
			);
		}

	}

}
