package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.main.BudgetBookDao;
import com.areum.moneymanager.dto.common.ImageDTO;
import com.areum.moneymanager.entity.BudgetBook;
import com.areum.moneymanager.enums.RegexPattern;
import com.areum.moneymanager.exception.ErrorException;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.areum.moneymanager.exception.code.ErrorCode.BUDGET_REGISTER_IMAGE;


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
 *		</tbody>
 * </table>
 */
@Service("budgetImage")
public class ImageServiceImpl {

	@Value("${image.budgetBook.downPath}")
	private String downPath;

	private final int MAX_IMAGE = 3;
	private final BudgetBookDao budgetBookDAO;

	private final Logger logger = LogManager.getLogger(this);

	public ImageServiceImpl( BudgetBookDao budgetBookDAO ) {
		this.budgetBookDAO = budgetBookDAO;
	}



	/**
	 * 회원별로 등록할 수 있는 이미지 개수를 반환합니다.
	 *
	 * @param memberId		회원 고유번호
	 * @return	회원의 등록 가능한 이미지 개수
	 */
	public  int getLimitImageCount( String memberId ) {
		Integer limitSize = budgetBookDAO.findImageLimit(memberId);

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
				String safeName = originName.replaceAll(RegexPattern.BUDGET_IMAGE.getPattern(), "");
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
	 * @param budgetBook					등록할 가계부 정보
	 * @param image							등록할 가계부 이미지
	 * @param index							등록할 이미지 순서
	 * @throws IOException	가계부 이미지가 없을 시
	 */
	public void saveImage( BudgetBook budgetBook, MultipartFile image, int index ) throws IOException {
		if( Objects.isNull(budgetBook) || Objects.isNull(image) ) {
			throw new ErrorException( BUDGET_REGISTER_IMAGE );
		}

		//폴더와 저장할 이미지 얻은 후 서버에 저장
		File directory = makeDirectory( budgetBook.getMember().getId(), budgetBook.getBookDate().substring(0, 4) );
		String saveName = changeImageName( budgetBook.getId(), budgetBook.getBookDate(), index, image.getOriginalFilename() );

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
	private File makeDirectory( String memberId, String year ) {
		String url = downPath + memberId + "/" + year;

		File directory = new File(url);

		if( !directory.exists() ) {
			directory.mkdirs();
		}

		return directory;
	}



	/**
	 * 서버에 저장할 이미지 이름을 변경 후 반환합니다.
	 *
	 * @param id 									가계부 번호
	 *  @param date								가계부 작성날짜
	 * @param index							이미지 순서
	 * @param imageName					가계부 이미지 이름
	 *
	 *
	 * @return 변경된 이미지 이름
	 */
	private String changeImageName( Long id, String date, int index, String imageName ) {
		String ext = FilenameUtils.getExtension(imageName);
		String originName = FilenameUtils.getBaseName(imageName);

		return String.format("%d_%s_%s_%d.%s", id, date, originName, ++index, ext);
	}



	/**
	 * 특정 가계부에 등록된 이미지 경로를 반환합니다.<br>
	 * 만약 가계부에 등록된 이미지가 없으면 null을 반환합니다.
	 *
	 * @param budgetBook  			가계부 정보
	 * @return 사진과 폴더가 존재하면 '경로+사진', 존재하지 않으면 null
	 */
	public List<String> findImageUrl( BudgetBook budgetBook ) {
		List<String> imageUrls = new ArrayList<>();

		//budgetBook 이미지 담기
		List<String> budgetBookImages = Arrays.asList( budgetBook.getImage1(), budgetBook.getImage2(), budgetBook.getImage3() );
		for( int i=0; i<budgetBookImages.size(); i++ ) {
			String image = budgetBookImages.get(i);

			if( !Objects.isNull(image)) {
				String year = budgetBook.getBookDate().substring(0, 4);
				String name = changeImageName( budgetBook.getId(), budgetBook.getBookDate(), i ,image );

				imageUrls.add( String.format("%s/%s/%s", budgetBook.getMember().getId(), year, name) );
			}else {
				imageUrls.add( null );
			}
		}

		return imageUrls;
	}



	/**
	 * 가게부 이미지의 변경여부를 확인합니다. <br>
	 * 서버에 저장된 이미지(serverImage)가 있는 상태에서 저장할 이미지(clientImage) 여부에 따라 다르게 동작합니다.<br>
	 *
	 * @param memberId			회원 고유번호
	 * @param budgetBook 		변경할 가계부 정보
	 * @param imageList			변경할 이미지 정보
	 *
	 * @throws IOException  사용자가 업로드한 이미지 문제 시
	 */
	public void changeImage( String memberId, BudgetBook budgetBook, List<ImageDTO> imageList ) throws IOException {
		File directory = makeDirectory( memberId, budgetBook.getBookDate().substring(0, 4) );

		//기존 이미지 삭제
		boolean isDelete = deleteImage( directory, budgetBook.getId() );
		if( isDelete ) {
			logger.info("삭제완료");
			budgetBookDAO.updateImage( memberId, budgetBook );

			for( int i=0; i < imageList.size(); i++ ) {
				if( Objects.nonNull( imageList.get(i).getFile() ) ) {
					logger.info("저장시작");
					saveImage( budgetBook, imageList.get(i).getFile(), i );
				}
			}
		}
	}



	/**
	 * 회원이 이전에 변경한 가계부 사진 삭제하는 메서드
	 *
	 * @param folder	사진이 저장된 파일
	 */
	public boolean deleteImage( File folder, Long id ) {
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
