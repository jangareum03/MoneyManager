package com.moneymanager.service.ledger;

import com.moneymanager.domain.global.dto.StoredFile;
import com.moneymanager.domain.ledger.dto.request.LedgerImageRequest;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.service.file.FileCommandService;
import com.moneymanager.service.file.LedgerImageStorageStrategy;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.moneymanager.exception.error.ErrorCode.LEDGER_ETC_DB_ERROR;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.ledger<br>
 * 파일이름       : LedgerImageCommandService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 5. 24<br>
 * 설명              : 가계부 이미지 정보를 변경하는 클래스
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
 * 		 	  <td>26. 5. 24</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@AllArgsConstructor
public class LedgerImageCommandService {

	private final FileCommandService fileCommandService;
	private final LedgerImageStorageStrategy imageStorageStrategy;

	private final LedgerImageRepository imageRepository;

	public void processImages(Ledger ledger, LedgerImageRequest request) {
		//1. 기존 이미지 조회
		List<LedgerImage> images = imageRepository.findByLedgerId(ledger.getId());

		boolean hasNewImages = request.hasImage();
		boolean hasStoredImages = !images.isEmpty();

		//2. 새로운 이미지, 기존 이미지 모두 없으면 종료
		if(!hasNewImages && !hasStoredImages) {
			return;
		}

		//3. 새로운 이미지, 기존 이미지 모두 있으면 수정 메서드 호출
		if(hasNewImages && hasStoredImages) {
			updateFiles(ledger, request, images);
			return;
		}

		//4. 새로운 이미지가 있으면 저장 메서드 호출
		if(hasNewImages) {
			saveFiles(ledger, request);
			return;
		}

		//5. 기존 이미지만 있으면 삭제 메서드 호출
		deleteFiles(ledger, images);
	}

	private void saveFiles(Ledger ledger, LedgerImageRequest request) {
		List<Path> successSaved = new ArrayList<>();
		List<LedgerImage> ledgerImages = new ArrayList<>();

		try{
			int index = 0;

			for(MultipartFile file : request.getImages()) {
				StoredFile storedFile = imageStorageStrategy.createStoredFile(ledger, file.getOriginalFilename());

				fileCommandService.createDirectory(storedFile.getFullPath().getParent());
				fileCommandService.upload(file, storedFile.getFullPath());

				successSaved.add(storedFile.getFullPath());
				ledgerImages.add(LedgerImage.create(ledger.getId(), storedFile.getRelativePath(), ++index));
			}

			save(ledger, ledgerImages);
		}catch (BusinessException e) {
			for(Path path : successSaved) {
				fileCommandService.delete(path);
			}

			throw e.withUserMessage("이미지 저장 중 문제가 발생했습니다. 다시 시도해주세요.");
		}
	}

	private void save(Ledger ledger, List<LedgerImage> ledgerImages) {
		try{
			imageRepository.saveAll(ledgerImages);
		}catch (DataAccessException e) {
			throw BusinessException.of(
							LEDGER_ETC_DB_ERROR,
							"이미지 저장 실패   |   reason=DB추가실패   |   object=LedgerImage   |   value={memberId: " + ledger.getMemberId() + ", ledgerId: " + ledger.getId() + "}"
					)
					.withCause(e);
		}
	}

	private void updateFiles(Ledger ledger, LedgerImageRequest request, List<LedgerImage> imageList) {
		saveFiles(ledger, request);
		deleteFiles(ledger, imageList);
	}

	private void deleteFiles(Ledger ledger, List<LedgerImage> imageList) {
		try{
			delete(ledger, imageList.size());

			imageList.stream()
					.map(image ->
							imageStorageStrategy.generateAbsolutePath(image.getImagePath())
					)
					.forEach(fileCommandService::delete);
		}catch (BusinessException e) {
			throw e.withUserMessage("이미지 삭제 중 문제가 발생했습니다. 다시 시도해주세요.");
		}
	}

	private void delete(Ledger ledger, int imageSize) {
		int deleted  = imageRepository.deleteByLedgerId(ledger.getId());

		if(deleted != imageSize) {
			throw BusinessException.of(
							LEDGER_ETC_DB_ERROR,
							"이미지 삭제 실패   |   reason=DB삭제실패   |   object=LedgerImage   |   value={memberId: " + ledger.getMemberId() + ", ledgerId: " + ledger.getId() + "}"
					);
		}
	}

}
