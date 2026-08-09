package com.moneymanager.global.validation;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

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

	}

	protected void checkExtension(String fileName, List<String> allowedExtensions) {
		String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
	}

	protected void checkHeader(MultipartFile file, List<String> allowedHeaders) {
		try(InputStream is = file.getInputStream()){
			byte[] header = new byte[8];
			int read = is.read(header);

			String hex = byteToHex(Arrays.copyOf(header, 4));

		}catch (IOException e) {

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
	}

}
