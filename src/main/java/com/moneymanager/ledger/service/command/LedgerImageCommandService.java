package com.moneymanager.ledger.service.command;

import com.moneymanager.delete.domain.global.dto.StoredFile;
import com.moneymanager.global.file.FileCommandService;
import com.moneymanager.ledger.domain.dto.request.LedgerImageRequest;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.entity.LedgerImage;
import com.moneymanager.ledger.repository.LedgerImageRepository;
import com.moneymanager.ledger.service.strategy.LedgerImageStorageStrategy;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

		int index = 0;

		for(MultipartFile file : request.getImages()) {
			StoredFile storedFile = imageStorageStrategy.createStoredFile(ledger, file.getOriginalFilename());

			fileCommandService.createDirectory(storedFile.getFullPath().getParent());
			fileCommandService.upload(file, storedFile.getFullPath());

			successSaved.add(storedFile.getFullPath());
			ledgerImages.add(LedgerImage.create(ledger.getId(), storedFile.getRelativePath(), ++index));
		}

		save(ledger, ledgerImages);
	}

	private void save(Ledger ledger, List<LedgerImage> ledgerImages) {
		imageRepository.saveAll(ledgerImages);
	}

	private void updateFiles(Ledger ledger, LedgerImageRequest request, List<LedgerImage> imageList) {
		saveFiles(ledger, request);
		deleteFiles(ledger, imageList);
	}

	private void deleteFiles(Ledger ledger, List<LedgerImage> imageList) {
		delete(ledger, imageList.size());

		imageList.stream()
				.map(image ->
						imageStorageStrategy.generateAbsolutePath(image.getImagePath())
				)
				.forEach(fileCommandService::delete);
	}

	private void delete(Ledger ledger, int imageSize) {
		int deleted  = imageRepository.deleteByLedgerId(ledger.getId());
	}

}
