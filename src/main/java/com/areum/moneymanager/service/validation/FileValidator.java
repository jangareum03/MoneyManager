package com.areum.moneymanager.service.validation;

import com.areum.moneymanager.dto.request.ValidRequestDTO;
import com.areum.moneymanager.exception.code.ErrorCode;
import com.areum.moneymanager.exception.custom.ClientException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.service.validation<br>
 *  * 파일이름       : FileValidator<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 24<br>
 *  * 설명              : 파일 관련 검증 로직을 처리하는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 7. 24</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>최초 생성(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Component
public class FileValidator {

	private static final List<String> SUPPORT_FILE = List.of("jpg", "jpeg", "png", "gif");
	private static  final  int MAX_SIZE = 1024 * 1024 * 5;	//5MD


	/**
	 * 프로필 파일이 정상적인지 확인합니다.
	 *
	 * @param	requestDTO				요청정보를 담은 객체
	 * @throws	ClientException 		이미지가 유효하지 않을 경우 발생
	 */
	public <T> void validateProfileImage( ValidRequestDTO<T> requestDTO ) {
		MultipartFile file = (MultipartFile) requestDTO.getRequestData();
		List<ErrorCode> errorCodes = ErrorCode.getGroupByPrefix(requestDTO.getErrorPrefix());

		//파일이 없는 경우
		if( Objects.isNull(file) || file.isEmpty() ) {
			throw new ClientException( ErrorCode.getByName(errorCodes, "MISSING"), requestDTO );
		}

		//파일 사이즈가 0인 경우
		if( file.getSize() == 0 ) {
			throw new ClientException( ErrorCode.getByName(errorCodes,"EMPTY"), requestDTO);
		}

		//미지원 파일
		if( !checkFileExtension(file) ) {
			throw new ClientException( ErrorCode.getByName(errorCodes, "NOT_SUPPORTED") , requestDTO);
		}

		//파일 사이즈가 큰 경우
		if( file.getSize() > MAX_SIZE ) {
			throw new ClientException( ErrorCode.getByName(errorCodes,"SIZE_EXCEEDED"), requestDTO);
		}
	}


	/**
	 * 파일이 지원하는 파일인지 확인합니다.
	 *
	 * @param file		업로드된 MultipartFile 객체
	 * @return true : 지원하는 파일 / false : 미지원하는 파일
	 */
	private boolean checkFileExtension( MultipartFile file ) {
		String fileName = file.getOriginalFilename();
		String fileExt = Objects.requireNonNull(fileName).substring( fileName.lastIndexOf(".") + 1 );

		return true;
	}
}
