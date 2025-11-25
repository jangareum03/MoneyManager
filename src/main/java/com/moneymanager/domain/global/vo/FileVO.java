package com.moneymanager.domain.global.vo;

import lombok.Builder;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * <p>
 * 패키지이름    : com.moneymanager.vo<br>
 * 파일이름       : FileVO<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 9. 2.<br>
 * 설명              : 파일 값을 나타내는 클래스
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
 * 		 	  <td>25. 9. 2.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Value
public class FileVO {
	String fileName;					//서버에 저장된 파일명(UUID)
	String originalName;			//원본 파일명
	String extension;				//확장자
	String path;						//저장 경로
	long size;							//파일 크기
	LocalDateTime uploadAt;	//업로드된 날짜와 시간


	@Builder
	public FileVO(String fileName, String originalName, String extension, String path, long size, LocalDateTime uploadAt) {
		this.fileName = fileName;
		this.originalName = originalName;
		this.extension = extension;
		this.path = path;
		this.size = size;
		this.uploadAt = uploadAt;
	}


	public static FileVO from(MultipartFile file, String basePath) {
		String  originalName = file.getOriginalFilename();
		String extension = "";

		if(originalName != null && originalName.contains(".")) {
			int index = originalName.lastIndexOf(".") + 1;

			extension = originalName.substring(index);
		}




		return FileVO.builder()
				.build();
	}
}
