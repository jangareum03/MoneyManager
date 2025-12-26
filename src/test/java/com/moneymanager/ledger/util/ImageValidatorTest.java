package com.moneymanager.ledger.util;

import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.service.main.validation.ImageValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.image<br>
 * 파일이름       : ImageValidatorTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 24<br>
 * 설명              : 파일을 검증하는 테스트 클래스
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
 * 		 	  <td>25. 12. 24.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class ImageValidatorTest {
	//=================================================
	// validate() 테스트
	//=================================================
	@DisplayName("빈 파일이면 ClientException 예외가 발생한다.")
	@Test
	void 파일_비어있으면_예외발생(){
		//given
		MockMultipartFile file = new MockMultipartFile(
			"file",
			"empty.png",
				"image/png",
				new byte[0]
		);

		//when&then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> ImageValidator.validate(file))
				.satisfies(e -> {
					ErrorDTO errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_PHOTO_MISSING);
					assertThat(errorDTO.getMessage()).isEqualTo( "저장할 가계부 이미지가 없습니다.");
				});
	}

	@DisplayName("파일 사이즈가 최대값을 넘기면 ClientException 예외가 발생한다.")
	@Test
	void 파일_사이즈_최대값_넘으면_예외발생(){
		//given
		byte[] bigSize = new byte[ (int) ImageValidator.MAX_SIZE + 1];
		MockMultipartFile file = new MockMultipartFile(
			"file",
			"big.png",
			"image/png",
				bigSize
		);

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> ImageValidator.validate(file))
				.satisfies(e -> {
					ErrorDTO errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_PHOTO_SIZE_EXCEEDED);
					assertThat(errorDTO.getMessage()).isEqualTo( "이미지는 " + ImageValidator.MAX_SIZE + "까지만 가능합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo( file.getSize());
				});
	}

	@DisplayName("이미지가 손상되면 ClientException 예외가 발생한다.")
	@Test
	void 이미지가_아니면_예외발생(){
		//given
		MockMultipartFile file = new MockMultipartFile(
			"file",
				"notImage.jpg",
				"image/jpg",
				"안뇽".getBytes()
		);

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> ImageValidator.validate(file))
				.satisfies(e -> {
					ErrorDTO errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_PHOTO_SUPPORTED);
					assertThat(errorDTO.getMessage()).isEqualTo( "미지원한 이미지여서 저장할 수 없습니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(file.getContentType());
				});
	}

	@DisplayName("지원하지 않는 파일이면 ClientException 예외가 발생한다.")
	@Test
	void 파일_확장자가_허용된게_아니라면_예외발생() throws IOException{
		//given
		ClassPathResource resource = new ClassPathResource("images/test.gif");

		try (InputStream inputStream = resource.getInputStream()) {
			MockMultipartFile file = new MockMultipartFile(
					"file",
					"test.gif",
					"image/gif",
					inputStream
			);

			assertThatExceptionOfType(ClientException.class)
					.isThrownBy(() -> ImageValidator.validate(file))
					.satisfies(e -> {
						ErrorDTO errorDTO = e.getErrorDTO();

						assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_PHOTO_SUPPORTED);
						assertThat(errorDTO.getMessage()).isEqualTo( "미지원한 이미지여서 저장할 수 없습니다.");
						assertThat(errorDTO.getRequestData()).isEqualTo( file.getContentType());
					});
		}
	}

	@DisplayName("파일이 정상이면 예외가 발생하지 않고 통과한다.")
	@Test
	void 파일_정상이면_검증통과() throws IOException{
		//given
		ClassPathResource resource = new ClassPathResource("images/test.png");

		try (InputStream inputStream = resource.getInputStream()) {
			MockMultipartFile file = new MockMultipartFile(
					"file",
					"test.png",
					"image/png",
					inputStream
			);

			assertThatCode(() -> ImageValidator.validate(file))
					.doesNotThrowAnyException();
		}
	}
}
