package com.moneymanager.service.main;

import com.moneymanager.dao.main.LedgerImageDao;
import com.moneymanager.dao.member.MemberInfoDaoImpl;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.global.dto.ImageDTO;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.domain.ledger.vo.LedgerDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


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
@RequiredArgsConstructor
public class ImageServiceImpl {

	@Value("${image.ledger.downPath}")
	private String downPath;
	@Getter
	private final int MAX_IMAGE = 3;

	private final MemberInfoDaoImpl memberInfoDao;
	private final LedgerImageDao imageDao;


	/**
	 * 가계부를 저장할 서버의 절대경로를 얻습니다.
	 *
	 * @return	저장될 절대 경로 문자열
	 */
	public String getBaseImagePath() {
		return downPath;
	}


	/**
	 * 회원이 한 가계부에 등록할 수 있는 이미지 최대 허용 개수를 계산하여 반환합니다.
	 * <p>
	 *     데이터베이스에서 회원의 이미지 제한값을 조회한 뒤, 시스템에서 지정한 최대 허용치({@code MAX_IMAGE})와 비교하여 더 작은 값을 반환합니다.
	 *     만약 회원이 존재하지 않아 데이터베이스에서 조회를 할 수 없다면 기본값인 1이 반환됩니다.
	 * </p>
	 *
	 * @param memberId		조회할 회원 ID
	 * @return	이미지 업로드 가능한 이미지 개수
	 */
	public int getLimitImageCount( String memberId ) {
		try{
			Integer count = memberInfoDao.findImageLimit(memberId);

			return Math.min( count, MAX_IMAGE );
		}catch ( EmptyResultDataAccessException e ) {
			return 1;
		}
	}


	/**
	 * 회원이 사용할 수 있는 가계부 이미지 슬롯 상태를 반환합니다.
	 * <p>
	 *     회원이 등록할 수 있는 최대 이미지 개수({@code MAX_IMAGE})를 기준으로,
	 *     실제 사용 가능한 슬롯과 사용 불가능한 슬롯을 {@link Boolean} 값으로 표현합니다.
	 * </p>
	 *
	 * @param memberId		이미지 슬롯 정보를 불러올 회원 ID
	 * @return	이미지 슬롯 사용 가능 여부를 나타내는 Boolean 리스트
	 */
	public List<Boolean> getImageSlots(String memberId) {
		int usedCount = getLimitImageCount(memberId);

		return IntStream.range(0, MAX_IMAGE)
				.mapToObj( s -> s < usedCount )
				.collect(Collectors.toList());
	}



	/**
	 * 가계부 ID({@code id})에 해당하는 이미지 리스트를 반환합니다.
	 * <p>
	 * 데이터베이스에서 특정 가계부에 등록된 이미지 정보를 조회한 뒤, 정렬 순서({@code sortOrder}) 기준으로 오름차순으로 정렬합니다.
	 * 서비스 정책상 최대 이미지 개수({@code MAX_IMAGE})와 사용자별로 등록 가능한 이미지 개수 제한({@code limit})이 다르기 때문에
	 *	조회된 이미지는 {@code limit}만큼만 사용하고 최종 반환 리스트 크기는 항상 {@code MAX_IMAGE}로 고정됩니다.
	 * </p>
	 *<p>
	 * 이미지가 존재하지 않은 인덱스는 {@code null}로 채워지며, 이는 화면단에서 고정된 이미지 슬록을 표현할 수 있습니다.
	 *</p>
	 *
	 * @param id			이미지 조회 대상 가계부 ID
	 * @param limit		사용자에게 허용된 최대 이미지 개수
	 * @return	{@code MAX_IMAGE} 크기로 구성된 {@link LedgerImage} 객체 리스트
	 */
	public List<LedgerImage> getImageListByLedger(Long id, int limit) {
		List<LedgerImage> ledgerImages = imageDao.findImageListByLedger(id);

		//DB 조회 결과를 order 오름차순으로 정렬
		List<LedgerImage> sortedImages = ledgerImages.stream()
				.sorted(Comparator.comparingInt(LedgerImage::getSortOrder))
				.limit(limit)
				.collect(Collectors.toList());

		//결과 리스트는 MAX_IMAGE만큼 크기 결정
		List<LedgerImage> result = new ArrayList<>(MAX_IMAGE);

		for( int i=0; i<MAX_IMAGE; i++ ) {
			if( i < sortedImages.size() ) {
				result.add(sortedImages.get(i));
			}else {
				result.add(null);
			}
		}

		return result;
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
		LocalDate date = new LedgerDate(ledger.getDate()).getTransactionDate();

		java.io.File directory = makeDirectory( ledger.getMemberId(), date.getYear());
		String saveName = changeImageName( ledger.getId(), date, index, image.getOriginalFilename() );

		java.io.File saveImage = new java.io.File( directory, saveName );

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
	private java.io.File makeDirectory(String memberId, int year ) {
		String url = downPath + memberId + "/" + year;

		java.io.File directory = new java.io.File(url);

		if( !directory.exists() ) {
			directory.mkdirs();
		}

		return directory;
	}


	//서버에 저장할 이미지 이름을 변경합니다.
	private String changeImageName(String id, LocalDate date, int index, String imageName ) {
		String ext = FilenameUtils.getExtension(imageName);
		String originName = FilenameUtils.getBaseName(imageName);

		return String.format("%s_%s_%s_%d.%s", id, date, originName, ++index, ext);
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
		LocalDate date = new LedgerDate(ledger.getDate()).getTransactionDate();
		java.io.File directory = makeDirectory( memberId, date.getYear());

		//기존 이미지 삭제
		boolean isDelete = deleteImage( directory, ledger.getId() );
		if( isDelete ) {
			//TODO: 이미지 수정

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
	public boolean deleteImage(java.io.File folder, String id ) {
		//폴더가 없거나 폴더가 아닌 경우
		if( !folder.exists() || !folder.isDirectory() ) {
			return false;
		}

		java.io.File[] files = folder.listFiles();

		//파일 목록이 없는 경우
		if( files == null ) {
			return false;
		}

		for( java.io.File file : files ) {
			if(  file.isFile() && file.getName().contains(String.valueOf(id)) ) {
				return file.delete();
			}
		}

		return true;
	}
}
