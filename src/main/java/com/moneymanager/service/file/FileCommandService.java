package com.moneymanager.service.file;

import com.moneymanager.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.moneymanager.exception.error.ErrorCode.FILE_ETC_RESOURCE_ERROR;
import static com.moneymanager.exception.error.ErrorCode.FILE_ETC_UNKNOWN;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.file<br>
 * 파일이름       : FileCommandService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 10<br>
 * 설명              : 파일을 변경하는 클래스
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
 * 		 	  <td>26. 1. 10.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@Slf4j
public class FileCommandService {

	public void createDirectory(Path directory) {
		try{
			Files.createDirectories(directory);
		}catch (IOException e) {
			throw BusinessException.of(
					FILE_ETC_UNKNOWN,
					"폴더 생성 실패   |   reason=폴더생성실패   |   object=file   |   value=" + directory
			).withCause(e);
		}
	}

	public void upload(MultipartFile source, Path target) {
		try{
			source.transferTo(target);
		}catch (IOException e) {
			throw BusinessException.of(
					FILE_ETC_RESOURCE_ERROR,
					"파일 저장 실패   |   reason=저장불가   |   object=file   |   value=" + target
			).withCause(e);
		}
	}

	public void delete(Path path) {
		try {
			Files.deleteIfExists(path);
		}catch (IOException e) {
			log.warn("파일 삭제 실패   |   reason=삭제불가   |   object=file   |   value={}", path);
		}
	}

}
