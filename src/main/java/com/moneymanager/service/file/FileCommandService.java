package com.moneymanager.service.file;

import com.moneymanager.domain.global.dto.StoredFile;
import com.moneymanager.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.moneymanager.exception.error.ErrorCode.*;

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

	public <T> StoredFile storeFile(MultipartFile file, T target, FileNamingStrategy<T> strategy) {
		String rootPath = strategy.getRootPath();
		String storedName = strategy.generateStoredName(file.getOriginalFilename());
		String directoryPath = strategy.generateDirectoryPath(target);
		String dbPath = strategy.generateDbPath(target, storedName);

		String fullDirPath = Path.of(rootPath, directoryPath).toString();

		File directory = new File(fullDirPath);
		createDirectory(directory);

		File savedFile = new File(directory, storedName);

		upload(file, savedFile);

		return new StoredFile(savedFile.getAbsolutePath(), dbPath);
	}

	//폴더 생성
	private void createDirectory(File directory) {
		if(directory.exists()) {
			if(!directory.isDirectory()) {
				throw BusinessException.of(
						FILE_COLLISION_ALREADY_EXISTS,
						"폴더 생성 실패   |   reason=중복데이터   |   object=file   |   value=" + directory.getAbsolutePath()
				);
			}

			return;
		}

		if(!directory.mkdirs()) {
			if(directory.exists() && directory.isDirectory()) {
				return;
			}

			throw BusinessException.of(
					FILE_ETC_UNKNOWN,
					"폴더 생성 실패   |   reason=폴더생성실패   |   object=file   |   value=" + directory.getAbsolutePath()
			);
		}
	}

	private void upload(MultipartFile oldFile, File newFile) {
		try{
			oldFile.transferTo(newFile);
		}catch (IOException e) {
			throw BusinessException.of(
					FILE_ETC_RESOURCE_ERROR,
					"파일 처리 실패   |   reason=파일저장불가   |   object=file   |   value=" + newFile.getAbsolutePath()
			).withCause(e);
		}
	}

	public void deleteFiles(List<File> files) {
		for(File file : files) {
			if(!file.delete()) {
				log.warn("파일 처리 실패   |   reason=파일삭제불가   |   object=file   |   value={}", file.getAbsolutePath());
			}
		}
	}
}
