package com.moneymanager.global.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

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

		}
	}

	public void upload(MultipartFile source, Path target) {
		try{
			source.transferTo(target);
		}catch (IOException e) {

		}
	}

	public void delete(Path path) {
		try {
			Files.deleteIfExists(path);
		}catch (IOException e) {

		}
	}

	private String getCause(IOException e) {
		if (e instanceof AccessDeniedException) {
			return "접근 권한 없음";
		}

		if (e instanceof FileAlreadyExistsException) {
			return "존재하는 파일 또는 폴더";
		}

		if (e instanceof NoSuchFileException) {
			return "찾을 수 없는 파일 또는 폴더";
		}

		return "파일 시스템 오류";
	}

}
