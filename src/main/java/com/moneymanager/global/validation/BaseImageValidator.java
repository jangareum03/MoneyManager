package com.moneymanager.global.validation;

import com.moneymanager.global.exception.exception.ValidationException;
import com.moneymanager.global.log.LogContent;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.moneymanager.global.exception.code.CommonErrorCode.*;
import static com.moneymanager.global.util.string.StringUtil.isNullOrBlank;

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

	private final String work = "이미지 파일 검증";

	protected void validateContentType(String contentType) {
		if(contentType == null || !contentType.contains("image")) {
			throw ValidationException.of(
					UNSUPPORTED_FILE_TYPE,
				LogContent.of(
						work,
						MultipartFile.class,
						"contentType",
						contentType
				)
			);
		}
	}

	protected void validateExtension(MultipartFile file, List<String> allowedExtensions) {
		String fileName = file.getOriginalFilename();

		if(isNullOrBlank(fileName)) {
			throw ValidationException.of(
					REQUIRED_VALUE,
					LogContent.of(
							work,
							MultipartFile.class,
							"originalFilename",
							fileName
					)
			);
		}

		String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

		if(!allowedExtensions.contains(ext)) {
			throw ValidationException.of(
					UNSUPPORTED_FILE_TYPE,
					LogContent.of(
							work,
							MultipartFile.class,
							"originalFilename",
							fileName
					).withOption("allowed", allowedExtensions)
			);
		}
	}

	protected void validateHeader(MultipartFile file, List<String> allowedHeaders) throws IOException {
		try(InputStream is = file.getInputStream()){
			byte[] header = is.readNBytes(4);

			String hex = byteToHex(header);

			if(!allowedHeaders.contains(hex)) {
				throw ValidationException.of(
						UNSUPPORTED_FILE_TYPE,
						LogContent.of(
								work,
								MultipartFile.class,
								"header",
								hex
						)
				);
			}
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

	protected void validateSize(long size) {
		long max = 5 * 1024 * 1024;

		if(size > max) {
			throw ValidationException.of(
					FILE_TOO_LARGE,
					LogContent.of(
							work,
							MultipartFile.class,
							"size",
							size
					).withOption("max", max)
			);
		}
	}

}
