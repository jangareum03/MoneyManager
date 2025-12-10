package com.moneymanager.service.main;

import com.moneymanager.dao.main.LedgerDao;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.global.dto.ImageDTO;
import com.moneymanager.domain.global.enums.RegexPattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.service.main<br>
 *  * 파일이름       : ImageServiceImpl<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 가계부 이미지 관련 비즈니스 로직을 처리하는 클래스
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
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 *		 	</tr>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 12. 08</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>[메서드 삭제] findImageUrl</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Slf4j
@Service("ledgerImage")
public class ImageServiceImpl {

	@Value("${image.ledger.downPath}")
	private String downPath;

	private final int MAX_IMAGE = 3;
	private final LedgerDao ledgerDAO;

	public ImageServiceImpl( LedgerDao ledgerDao ) {
		this.ledgerDAO = ledgerDao;
	}


	/**
	 * 가계부를 저장할 서버의 절대경로를 얻습니다.
	 *
	 * @return	저장될 절대 경로 문자열
	 */
	public String getBaseImagePath() {
		return downPath;
	}



	/**
	 * 회원별로 등록할 수 있는 이미지 개수를 반환합니다.
	 *
	 * @param memberId		회원 고유번호
	 * @return	회원의 등록 가능한 이미지 개수
	 */
	public  int getLimitImageCount( String memberId ) {
		Integer limitSize = ledgerDAO.findImageLimit(memberId);

		if( Objects.isNull(limitSize) || limitSize <= 0 ) {
			return 1;
		}

		return limitSize;
	}



	/**
	 * 업로드한 이미지 파일 리스트를 List로 반환합니다.
	 *
	 * @param memberId					회원 식별번호
	 * @param fileList						업로드 파일 리스트
	 * @return	파일 List
	 */
	public List<ImageDTO> getImageList( String memberId, List<MultipartFile> fileList ) {
		List<ImageDTO> fileMetaList = new ArrayList<>();

		int min = getLimitImageCount(memberId);

		for( int i = 0; i < MAX_IMAGE; i++ ) {
			MultipartFile file = (i < fileList.size()) ? fileList.get(i) : null;

			if( i < min && file != null && !file.isEmpty() ) {
				//파일 이름
				String originName = FilenameUtils.getBaseName(file.getOriginalFilename());
				String safeName = originName.replaceAll(RegexPattern.LEDGER_IMAGE.getPattern(), "");
				String ext = FilenameUtils.getExtension(originName);

				String fileName = String.format("%s.%s", safeName, ext);

				fileMetaList.add( ImageDTO.builder().file( file ).fileName(fileName).fileExtension(ext).build() );
			} else {
				fileMetaList.add(null);
			}
		}

		return fileMetaList;
	}



	/**
	 *	가계부 이미지를 서버에 저장합니다.<P>
	 * 이미지 외 가계부 정보를 먼저 저장해야 하므로, 가계부 정보가 없으면 이미지를 저장할 수 없습니다.
	 *
	 * @param ledger					등록할 가계부 정보
	 * @param image							등록할 가계부 이미지
	 * @param index							등록할 이미지 순서
	 * @throws IOException	가계부 이미지가 없을 시
	 */
	public void saveImage(Ledger ledger, MultipartFile image, int index ) throws IOException {
		//폴더와 저장할 이미지 얻은 후 서버에 저장
		File directory = makeDirectory( ledger.getMember().getId(), ledger.getTransActionDate().getYear() );
		String saveName = changeImageName( ledger.getId(), ledger.getTransActionDate(), index, image.getOriginalFilename() );

		File saveImage = new File( directory, saveName );

		image.transferTo(saveImage);
	}



	/**
	 * 폴더를 생성합니다. <br>
	 * 생성하려는 폴더가 기존에 존재한다면 새로 생성하진 않습니다.
	 *
	 * @param memberId		회원번호
	 * @param year						가계부 날짜 년도
	 * @return	경로에 생성한 폴더
	 */
	private File makeDirectory( String memberId, int year ) {
		String url = downPath + memberId + "/" + year;

		File directory = new File(url);

		if( !directory.exists() ) {
			directory.mkdirs();
		}

		return directory;
	}


	//서버에 저장할 이미지 이름을 변경합니다.
	private String changeImageName(String id, LocalDate date, int index, String imageName ) {
		String ext = FilenameUtils.getExtension(imageName);
		String originName = FilenameUtils.getBaseName(imageName);

		return String.format("%d_%s_%s_%d.%s", id, date, originName, ++index, ext);
	}


	/**
	 * 가게부 이미지의 변경여부를 확인합니다. <br>
	 * 서버에 저장된 이미지(serverImage)가 있는 상태에서 저장할 이미지(clientImage) 여부에 따라 다르게 동작합니다.<br>
	 *
	 * @param memberId			회원 고유번호
	 * @param ledger 		변경할 가계부 정보
	 * @param imageList			변경할 이미지 정보
	 *
	 * @throws IOException  사용자가 업로드한 이미지 문제 시
	 */
	public void changeImage(String memberId, Ledger ledger, List<ImageDTO> imageList ) throws IOException {
		File directory = makeDirectory( memberId, ledger.getTransActionDate().getYear());

		//기존 이미지 삭제
		boolean isDelete = deleteImage( directory, ledger.getId() );
		if( isDelete ) {
			ledgerDAO.updateImage( memberId, ledger);

			for( int i=0; i < imageList.size(); i++ ) {
				if( Objects.nonNull( imageList.get(i).getFile() ) ) {
					saveImage(ledger, imageList.get(i).getFile(), i );
				}
			}
		}
	}



	/**
	 * 회원이 이전에 변경한 가계부 사진 삭제하는 메서드
	 *
	 * @param folder	사진이 저장된 파일
	 */
	public boolean deleteImage( File folder, String id ) {
		//폴더가 없거나 폴더가 아닌 경우
		if( !folder.exists() || !folder.isDirectory() ) {
			return false;
		}

		File[] files = folder.listFiles();

		//파일 목록이 없는 경우
		if( files == null ) {
			return false;
		}

		for( File file : files ) {
			if(  file.isFile() && file.getName().contains(String.valueOf(id)) ) {
				return file.delete();
			}
		}

		return true;
	}
}
