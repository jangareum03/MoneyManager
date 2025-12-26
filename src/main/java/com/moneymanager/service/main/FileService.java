package com.moneymanager.service.main;

import com.moneymanager.service.main.event.DeleteFileEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;


/**
 * <p>
 * 패키지이름    : com.moneymanager.service.main<br>
 * 파일이름       : FileService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 23.<br>
 * 설명              : 서버에서 관리할 파일들과 관련되 기능 클래스
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
 * 		 	  <td>25. 12. 23.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@Slf4j
public class FileService {

	@Value("${image.ledger.downPath}")
	private String downPath;

	//절대경로만
	public String getBasePath() {
		return downPath;
	}

	//상대경로만
	public String getRelativePath(String memberId, LocalDate date) {
		String year = String.valueOf(date.getYear());
		String month = String.format("%02d", date.getMonthValue());
		String day = String.format("%02d", date.getDayOfMonth());

		return
				File.separatorChar + memberId +
				File.separatorChar + year +
				File.separatorChar + month +
				File.separatorChar + day;
	}

	public File createFile( File folder, String name ) {
		return new File( folder, name );
	}

	//폴더 반환(없으면 생성)
	public File getFolder(String memberId, LocalDate date) {
		File folder = new File( getBasePath() + getRelativePath(memberId, date) );

		if( !folder.exists() ) {
			folder.mkdirs();
		}

		return folder;
	}

	public void saveFile(MultipartFile multipartFile, File file) throws IOException {
		multipartFile.transferTo(file);
	}


	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void deleteFile(DeleteFileEvent event) {
		for( String path : event.getPaths() ) {
			File file = new File(path);
			if( !file.delete() ) {
				log.debug("파일 삭제 실패: {}", path);
			}
		}
	}

}
