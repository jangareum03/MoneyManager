package com.moneymanager.service.main;

import com.github.f4b6a3.ulid.UlidCreator;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.service.main.event.DeleteFileEvent;
import com.moneymanager.service.main.strategy.FilePathStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static com.moneymanager.exception.ErrorUtil.createServerException;


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

	private final FilePathStrategy pathStrategy;

	public FileService( FilePathStrategy pathStrategy ) {
		this.pathStrategy = pathStrategy;
	}


	/**
	 * 지정한 폴더에 파일명을 가진 {@link File} 객체를 생성합니다.
	 * <p>
	 *     이미지 업로드 시, 저장할 파일의 전체 경로를 미리 구성하기 위해 사용되며,
	 *     이후 {@code saveFile}과 같은 메서드를 통해 실제 파일 저장이 수행됩니다.
	 * </p>
	 *
	 * @param folder	파일이 생성될 부모 폴더
	 * @param name	생성할 파일 이름
	 * @return	지정된 경로 정보를 가진 {@link File} 객체
	 */
	public File createFile( File folder, String name ) {
		return new File( folder, name );
	}


	/**
	 *	절대경로와 회원 ID와 날짜 정보를 조합한 경로에 폴더를 생성하거나 반환합니다.
	 *<p>
	 *    경로에 폴더가 존재하지 않는 경우 모든 하위 폴더를 포함하여 새로 생성하고,
	 *    이미 폴더가 존재한다면 기존 폴더를 그대로 반환합니다.
	 *</p>
	 *
	 * @param memberId	이미지가 저장될 회원 ID
	 * @param date			가계부 거래 날짜
	 * @return	생성되었거나 이미 존재하는 이미지 저장 폴더
	 */
	public File createFolder(String memberId, LocalDate date) {
		File folder = new File( pathStrategy.basePath() + buildFolderPath(memberId, date) );

		if( !folder.exists() ) {
			folder.mkdirs();
		}

		return folder;
	}


	/**
	 *	업로드된 {@link MultipartFile}을 지정한 {@link File} 경로에 실제 파일로 저장합니다.
	 *<p>
	 *     서버의 파일 시스템에 파일을 저장하며, 저장 중 경로 문제, 권한 문제 등이 발생할 수 있으며,
	 * 	 이러한 경우 {@link IOException}이 발생합니다.
	 *</p>
	 *
	 *
	 * @param multipartFile	클라이언트로부터 받은 파일
	 * @param file					서버에 저장될 실제 파일 경로 정보
	 * @throws IOException	파일 저장 중 오류가 발생하는 경우
	 */
	public void saveFile(MultipartFile multipartFile, File file) throws IOException {
		multipartFile.transferTo(file);
	}


	/**
	 * 회원ID와 날짜 정보를 기반으로 이미지 저장할 폴더 경로를 생성 후 반환합니다.
	 * <p>
	 *     생성된 경로는 회원별 구분할 수도 있으며, 날짜별로 정리하기 위한 용도입니다.
	 * </p>
	 * <p>
	 *     회원 ID가 {@code user01}이고 날짜가 {@code 2025-02-01}인 경우 아래와 같이 생성됩니다.
	 *		<pre>
	 *		 	/user01/2025/02/01
	 *		</pre>
	 * </p>
	 *
	 * @param memberId		이미지 업로드한 회원 ID
	 * @param date				가계부 거래 날짜
	 * @return	날짜 기준으로 구성된 이미지 상대 경로
	 */
	public String buildFolderPath(String memberId, LocalDate date) {
		String year = String.valueOf(date.getYear());
		String month = String.format("%02d", date.getMonthValue());
		String day = String.format("%02d", date.getDayOfMonth());

		return
				File.separatorChar + memberId +
				File.separatorChar + year +
				File.separatorChar + month +
				File.separatorChar + day;
	}


	/**
	 *	파일을 서버에 저장하기 위한 겹치지 않는 파일명을 생성합니다.
	 *<p>
	 *     파일 객체가 {@code null}이거나 원본 파일명이 없는 경우, {@link com.moneymanager.exception.custom.ServerException} 예외가 발생합니다.
	 *		파일명은 {@link UlidCreator}를 이용해 생성한 고유 식별자와 파일의 확장자를 조합하여 만들었습니다.
	 * </p>
	 *
	 * @param file	서버에 저장할 파일
	 * @return	고유 식별자와 확장자를 포함한 파일명
	 * @throws com.moneymanager.exception.custom.ServerException	저장할 파일 정보가 유효하지 않은 경우
	 */
	public String buildFileName(MultipartFile file) {
		if( file == null || file.getOriginalFilename() == null ) {
			throw createServerException(ErrorCode.STORAGE_FILE_INTERNAL, "파일이 없습니다.");
		}

		return String.format("%s.%s", File.separatorChar + UlidCreator.getUlid().toString(), getExtension(file));
	}


	//파일에서 확장자를 추출 후 반환
	private String getExtension(MultipartFile file){
		String fileName = file.getOriginalFilename();
		int index = fileName.lastIndexOf(".");

		if( index < 0 ) {
			throw createServerException(ErrorCode.STORAGE_FILE_INTERNAL, "확장자가 없는 파일입니다.");
		}

		return fileName.substring(index+1).toLowerCase();
	}


	/**
	 * 트랜잭션이 정상적으로 커밋이 된 후, 삭제 대상으로 전달된 파일들을 실제 서버에서 제거합니다.
	 * <p>
	 *     데이터베이스에서 트랜잭션이 성공된 후에만 파일 삭제가 수행됩니다.
	 *     이벤트 객체({@link DeleteFileEvent})에 포함된 파일 경로 목록의 파일들을 삭제합니다.
	 * </p>
	 * 파일 삭제 중 실패가 일어나더라도 디버그 레벨의 로그만 작성됩니다.
	 *
	 * @param event	삭제할 파일 경로 목록을 담은 이벤트 객체
	 */
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
