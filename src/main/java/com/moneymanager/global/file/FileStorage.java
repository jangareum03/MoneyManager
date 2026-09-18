package com.moneymanager.global.file;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * <p>
 * 패키지이름    : com.moneymanager.global.file.FileStorage<br>
 * 파일이름       : FileStorage<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 9. 18<br>
 * 설명              : 파일 저장 관련된 기능을 제공하는 클래스
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
 * 		 	  <td>26. 9. 18</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class FileStorage {

    public void createDirectory(Path path) throws IOException {
        Files.createDirectories(path);
    }

    public void saveFile(Path path, MultipartFile file, String saveFileName) throws IOException {
        Path savePath = path.resolve(saveFileName);

        file.transferTo(savePath);
    }

    public String extractFileExtension(String originalFilename) {
        int index = originalFilename.lastIndexOf('.');

        return originalFilename.substring(index);
    }

}