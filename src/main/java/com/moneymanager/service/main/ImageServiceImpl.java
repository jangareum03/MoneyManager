package com.moneymanager.service.main;

import com.moneymanager.dao.main.LedgerImageDao;
import com.moneymanager.dao.member.MemberInfoDaoImpl;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.domain.ledger.vo.LedgerDate;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.service.main.event.DeleteFileEvent;
import com.moneymanager.service.main.validation.ImageValidator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.moneymanager.exception.ErrorUtil.createServerException;


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
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 12. 23</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>
 *		 	      [메서드 삭제] createImageName, getBaseImagePath<br>
 *		 	      [필드 삭제] downPath
 *		 	  </td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Slf4j
@Service("ledgerImage")
@RequiredArgsConstructor
public class ImageServiceImpl {

	private final FileService fileService;
	private final ImageValidator imageValidator;
	private final MemberInfoDaoImpl memberInfoDao;
	private final LedgerImageDao imageDao;
	private final ApplicationEventPublisher	eventPublisher;


	@Getter
	private final int MAX_IMAGE = 3;


	/**
	 * 회원이 한 가계부에 등록할 수 있는 이미지 최대 허용 개수를 계산하여 반환합니다.
	 * <p>
	 *     데이터베이스에서 회원의 이미지 제한값을 조회한 뒤, 시스템에서 지정한 최대 허용치({@code MAX_IMAGE})와 비교하여 더 작은 값을 반환합니다.
	 *     만약 회원이 존재하지 않아 데이터베이스에서 조회를 할 수 없다면 기본값인 0이 반환됩니다.
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
			return 0;
		}
	}


	/**
	 * 회원이 사용할 수 있는 가계부 이미지 슬롯 상태를 반환합니다.
	 * <p>
	 *     회원이 등록할 수 있는 최대 이미지 개수({@code MAX_IMAGE})를 기준으로,
	 *     실제 사용 가능한 슬롯과 사용 불가능한 슬롯을 {@link Boolean} 값으로 표현합니다.
	 *     사용 가능한 슬롯은 {@code true}로, 사용 불가능한 슬롯은 {@code false}로 표현합니다.
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
	 *	가계부에 업로드 된 이미지를 변경합니다.
	 *<p>
	 *     기존 이미지와 새로 업로드된 이미지의 존재 여부에 따라 다음과 같은 방식으로 동작합니다.
	 *     <ul>
	 *         <li>새 이미지가 존재하면 기존 이미지와 상관없이 저장됩니다.</li>
	 *         <li>기존 이미지가 존재하면 기존 이미지와 DB 데이터를 삭제합니다.</li>
	 *     </ul>
	 *     결과적으로 가계부에는 새로 업로드된 이미지만 남게 됩니다.
	 *</p>
	 *
	 * @param ledger	이미지가 저장된 가계부 객체
	 * @param files		새로 업로드 될 이미지 파일 목록
	 */
	public void changeImages(Ledger ledger, List<MultipartFile> files ) {
		List<LedgerImage> images = imageDao.findImageListByLedger(ledger.getId());

		boolean hasOldImage = !images.isEmpty();
		boolean hasNewImage = files != null && !files.isEmpty();

		if( hasNewImage ) {
			upload( ledger, files );
		}

		if( hasOldImage && hasNewImage ) {
			deleteImages( ledger, images );
		}
	}


	/**
	 *	가계부에 업로드된 이미지 파일들을 서버에 저장하고, 이미지 메타데이터를 데이터베이스에 모두 저장합니다.
	 *<p>
	 *     파일 저장 중 {@link IOException}이 발생하면, 이미 저장된 파일들을 모두 삭제하고 {@link com.moneymanager.exception.custom.ServerException} 예외를 발생시킵니다.
	 *     데이터베이스 중 예외가 발생 시 트랜잭션은 롤백됩니다.
	 *</p>
	 *
	 * @param ledger		이미지가 저장될 가계부 정보를 담은 {@link Ledger} 객체
	 * @param files			업로드된 이미지 파일 목록
	 */
	@Transactional
	public void upload(Ledger ledger, List<MultipartFile> files ) {
		List<LedgerImage> newImages = new ArrayList<>();
		List<File> saveFiles = new ArrayList<>();
		int orderNum = 1;

		try{
			for( MultipartFile multipartFile : files ) {
				imageValidator.validate(multipartFile);

				//서버에 저장할 파일명 변경(중복 방지)
				String newName = fileService.buildFileName(multipartFile);

				//이미지를 서버에 저장
				LocalDate date = new LedgerDate(ledger.getDate()).getTransactionDate();
				File folder = fileService.createFolder(ledger.getMemberId(), date);
				File newFile = fileService.createFile( folder, newName );
				fileService.saveFile(multipartFile, newFile);
				saveFiles.add(newFile);

				newImages.add(
						LedgerImage.builder()
								.ledgerId(ledger.getId())
								.imagePath(fileService.buildFolderPath(ledger.getMemberId(), date))
								.sortOrder(orderNum++)
								.build()
				);
			}

			imageDao.insertAll(newImages);
		} catch (IOException e) {
			for(File file : saveFiles ) if( file.exists() ) file.delete();

			throw createServerException(ErrorCode.STORAGE_FILE_INTERNAL, "파일을 저장할 수 없습니다.", ledger.getId());
		}
	}



	/**
	 *	가계부에 등록된 이미지를 삭제합니다.
	 *<p>
	 *     가계부 날짜와 회원 정보를 기준으로 이미지 저장 폴더를 조회한 뒤,
	 *     전달된 이미지 목록에 포함된 파일들을 서버 파일 시스템에서 삭제합니다.
	 *</p>
	 * <p>
	 *     조건:
	 *     <ul>
	 *         <li>가계부 이미지 폴더인 경우에만 삭제 수행</li>
	 *         <li>폴더 내 파일목록이 없는 경우에는 종료</li>
	 *         <li>삭제 대상은 {@link LedgerImage}객체에 포함된 이미지 파일 경로</li>
	 *     </ul>
	 * </p>
	 *
	 * @param ledger		이미지가 삭제될 가계부 정보를 담은 {@link Ledger} 객체
	 * @param images		삭제할 이미지 정보 리스트
	 */
	@Transactional
	public void deleteImages(Ledger ledger, List<LedgerImage> images) {
		List<String> paths = new ArrayList<>();

		for( LedgerImage image : images ) {
			int result = imageDao.deleteByLedgerId(image.getId());

			if( result == 1 ) {
				//절대경로
				File folder = fileService.createFolder(ledger.getMemberId(), new LedgerDate(ledger.getDate()).getTransactionDate());
				String path = folder + image.getImagePath();
				paths.add(path);
			}
		}

		//트랜잭션 성공 후 커밋 완료
		eventPublisher.publishEvent(new DeleteFileEvent(paths));
	}
}