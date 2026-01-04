package com.moneymanager.service.main.validation;

import com.moneymanager.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.moneymanager.exception.ErrorUtil.createClientException;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.main.validation<br>
 * 파일이름       : ImageValidator<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 22<br>
 * 설명              : 가계부 이미지를 검증하는 클래스
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
 * 		 	  <td>25. 12. 22.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class ImageValidator {

	public final long MAX_SIZE = 1024 * 1024L;

	/**
	 * {@link MultipartFile} 객체의 유효성을 검증합니다.
	 * 유효성 검증이 실패하면 {@link com.moneymanager.exception.custom.ClientException}이 발생합니다.
	 * <p>
	 *     검증 조건:
	 *     <ul>
	 *         <li>파일이 null이면 {@link ErrorCode#LEDGER_PHOTO_MISSING} 발생</li>
	 *         <li>파일 용량이 허용범위보다 크면 {@link ErrorCode#LEDGER_PHOTO_SIZE_EXCEEDED} 발생</li>
	 *         <li>파일을 읽을수가 없으면  {@link ErrorCode#LEDGER_PHOTO_CORRUPTED} 발생</li>
	 *         <li>미지원한 파일 확장자라면  {@link ErrorCode#LEDGER_PHOTO_SUPPORTED} 발생</li>
	 *     </ul>
	 * </p>
	 *
	 * @param file		검증할 파일 객체
	 * @throws IOException	허용되지 않은 파일인 경우
	 */
	public void validate(MultipartFile file) throws IOException {
		if( file.isEmpty() ) {
			throw createClientException(ErrorCode.LEDGER_PHOTO_MISSING, "저장할 가계부 이미지가 없습니다.");
		}

		if( file.getSize() > MAX_SIZE ) {
			throw createClientException(ErrorCode.LEDGER_PHOTO_SIZE_EXCEEDED, "이미지는 " + MAX_SIZE + "까지만 가능합니다.", file.getSize());
		}

		validateExtension(file);
		validateImpairment(file);
	}

	private void validateImpairment(MultipartFile file) throws IOException{
		byte[] bytes = file.getBytes();

		try(InputStream is = new ByteArrayInputStream(bytes)) {
			BufferedImage image = ImageIO.read(is);
			if( image == null ) {
				throw createClientException(ErrorCode.LEDGER_PHOTO_CORRUPTED, "이미지가 손상되어 저장할 수 없습니다.");
			}
		}
	}

	private void validateExtension(MultipartFile file) {
		if( !List.of("image/jpeg", "image/png").contains(file.getContentType()) ){
			throw createClientException(ErrorCode.LEDGER_PHOTO_SUPPORTED, "미지원한 이미지여서 저장할 수 없습니다.", file.getContentType());
		}
	}
}