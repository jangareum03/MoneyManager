package com.moneymanager.service.ledger;

import com.github.f4b6a3.ulid.UlidCreator;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.file.FileCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 패키지이름    : com.moneymanager.service.ledger<br>
 * 파일이름       : LedgerCommandService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 10<br>
 * 설명              : 가계부 정보를 변경하는 클래스
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
@RequiredArgsConstructor
public class LedgerCommandService {

	private final SecurityUtil securityUtil;

	private final FileCommandService fileService;
	private final LedgerRepository ledgerRepository;
	private final LedgerImageRepository imageRepository;

	@Transactional
	public void registerLedger(LedgerWriteRequest request) {
		String memberId = securityUtil.getMemberId();

		//가계부 저장
		Long ledgerId = saveLedger(memberId, request);

		//이미지 저장
		if(hasImage(request.getImage())) {
			saveImage(ledgerId, request.getImage());
		}
	}

	private Long saveLedger(String memberId, LedgerWriteRequest request){
		Ledger ledger = createLedger(memberId, request);

		//TODO: SQLException 처리
		return ledgerRepository.insertLedger(ledger);
	}

	private Ledger createLedger(String memberId, LedgerWriteRequest request) {
		//가계부 고유ID 생성
		String ledgerCode = UlidCreator.getUlid().toString();

		return null;
	}


	//작성시 필요한 규칙 검증
	private void validateRegister(Ledger ledger) {
		//카테고리 코드가 숫자로만 이루어짐

		//고정여부 Y면 고정주기 있어야 함(반대로 N면 없어야 함)

		//가계부 거래 날짜 형식은 'YYYYMMDD' 여야 함

		//가격은 0 이하면 안됨

		//장소명이 있으면 도로명 주소는 필수, 상세주소는 선택
	}

	private boolean hasImage(List<MultipartFile> fileList) {
		return fileList != null && !fileList.isEmpty();
	}

	private void saveImage(Long ledgerId, List<MultipartFile> files) {
		List<File> saveFileList = new ArrayList<>();
		List<LedgerImage> images = new ArrayList<>();
		int sortedOrder = 1;

		try{
			for(MultipartFile file : files) {
				validateMultiFileForRegister(file);

				//서버에 파일 저장
				File saveFile = fileService.storeFile(file);
				saveFileList.add(saveFile);

				images.add(createLedgerImage(ledgerId, files, sortedOrder++));
			}

			imageRepository.insertAllImage(images);
		}catch (IOException e) {
			//서버에 저장한파일들 삭제
			for(File file : saveFileList) {
				if(file.exists()) file.delete();
			}
		}

	}

	private void validateMultiFileForRegister(MultipartFile file) {

	}

	private LedgerImage createLedgerImage(Long ledgerId, List<MultipartFile> files, int order) {
		Ledger ledger = ledgerRepository.selectLedgerById(ledgerId);

		//이미지 경로 생성
		String imagePath = null;

		return LedgerImage.builder()
				.ledgerId(ledger.getId())
				.imagePath(imagePath)
				.sortOrder(order)
				.build();
	}
}
