package com.moneymanager.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

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
public class FileCommandService {

	public File storeFile(MultipartFile multipartFile) throws IOException {
		File file = createFile(multipartFile);
		upload(multipartFile, file);

		return file;
	}

	public File createFile(MultipartFile multipartFile){
		return null;
	}

	public void upload(MultipartFile oldFile, File newFile) throws IOException {
		oldFile.transferTo(newFile);
	}
}
