package com.moneymanager.global.domain;

import lombok.Getter;

import java.nio.file.Path;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.domain<br>
 * 파일이름       : FileMetadata<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 20<br>
 * 설명              : 파일 속성을 정보를 나타내는 클래스
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
 * 		 	  <td>26. 8. 20</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Getter
public class FileMetadata {

    private final Path absolutePath;                    //절대경로 (파일 삭제용)
    private final Path relativePath;                    //상대경로 (DB 저장용)
    private final String originalFileName;          //원본 파일명
    private final String storedFileName;            //서버에 저장할 파일명
    private final String contentType;               //파일타입

    private FileMetadata(Path absolutePath, Path relativePath, String originalFileName, String storedFileName, String contentType) {
        this.absolutePath = absolutePath;
        this.relativePath = relativePath;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.contentType = contentType;
    }

    public static FileMetadata of(Path absolutePath, Path relativePath, String originalFileName, String storedFileName, String contentType) {
        return new FileMetadata(absolutePath, relativePath, originalFileName, storedFileName, contentType);
    }

}