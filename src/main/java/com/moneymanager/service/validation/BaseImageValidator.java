package com.moneymanager.service.validation;

import com.moneymanager.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static com.moneymanager.exception.error.ErrorCode.*;
import static com.moneymanager.utils.string.StringUtils.unwrap;

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
	protected void checkIsImage(String contentType) {
		if(contentType == null || !contentType.startsWith("image/")) {
			throw BusinessException.of(
					FILE_POLICY_NOT_ALLOWED,
					"이미지 파일만 업로드 할 수 있습니다.",
					"파일 검증 실패   |   reason=정책위반   |   object=file   |   field=contentType   |   policy=이미지가 아닌 다른 파일 업로드 시도  |   value=" + contentType
			);
		}
	}

	protected void checkExtension(String fileName, List<String> allowedExtensions) {
		if(fileName == null || !fileName.contains(".")) {
			throw BusinessException.of(
					FILE_INPUT_FORMAT,
					"잘못된 파일 이름입니다. 다른 파일로 진행해주세요.",
					"파일 검증 실패   |   reason=형식오류   |   object=imageFile   |   field=fileName   |   value=" + fileName
			);
		}

		String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

		if(!allowedExtensions.contains(ext)) {
			throw BusinessException.of(
					FILE_INPUT_FORMAT,
					"지원하지 않은 확장자입니다. 다른 파일로 진행해주세요.",
					"파일 검증 실패   |   reason=허용값아님   |   object=imageFile   |   field=fileName   |   allowedType=" + unwrap(allowedExtensions.toString(), "[", "]") + "   |   value=" + ext
			);
		}
	}

	protected void checkHeader(MultipartFile file, List<String> allowedHeaders) {
		try(InputStream is = file.getInputStream()){
			byte[] header = new byte[8];
			int read = is.read(header);

			if(read < 4) {
				throw BusinessException.of(
						FILE_INPUT_ETC,
						"손상된 파일입니다. 다른 파일로 진행해주세요.",
						"이미지 검증 실패   |   reason=길이오류   |   object=imageFile   |   field=header   |   expectedLength=4   |   value=" + read
				);
			}

			String hex = byteToHex(Arrays.copyOf(header, 4));

			if(!allowedHeaders.contains(hex)) {
				throw BusinessException.of(
						FILE_POLICY_NOT_ALLOWED,
						"지원하지 않은 파일입니다. 다른 파일로 진행해주세요.",
						"이미지 검증 실패   |   reason=허용값아님   |   object=imageFile   |   field=headerHex   |   allowedValues=" + unwrap(allowedHeaders.toString(), "[", "]") + "   |   value=" + hex
				);
			}
		}catch (IOException e) {
			throw BusinessException.of(
					FILE_INPUT_ETC,
					"손상된 파일입니다. 다른 파일로 진행해주세요.",
					"이미지 검증 실패   |   reason=파일읽기실패   |   object=imageFile"
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
			throw BusinessException.of(
					FILE_INPUT_EMPTY,
					"빈 파일은 업로드 할 수 없습니다.",
					String.format("이미지 검증 실패   |   reason=크기오류   |   object=imageFile   |   field=size   |   maxSize~%dMB   |   value=0", max)
			);
		}

		if(size > max) {
			throw BusinessException.of(
					FILE_POLICY_LIMIT_EXCEEDED,
					String.format("파일은 최대 %dMB까지만 업로드 가능합니다. 파일을 확인해주세요.", max),
					 String.format("이미지 검증 실패   |   reason=파일크기오류   |   object=imageFile   |   field=size   |   maxSize=%dMB   |   value=%d", max, size)
			);
		}
	}


}
