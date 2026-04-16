package com.moneymanager.service.ledger;

import com.moneymanager.domain.global.dto.StoredFile;
import com.moneymanager.domain.ledger.dto.request.LedgerImageRequest;
import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.exception.BusinessException;
import com.moneymanager.exception.error.ServiceAction;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.file.FileCommandService;
import com.moneymanager.service.file.LedgerImageNameStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.moneymanager.exception.error.ErrorCode.*;


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
@Slf4j
public class LedgerCommandService {

	private final SecurityUtil securityUtil;

	private final FileCommandService fileService;

	private final LedgerRepository ledgerRepository;
	private final LedgerImageRepository imageRepository;

	private final LedgerImageNameStrategy ledgerImageNameStrategy;

	@Transactional
	public void registerLedger(LedgerWriteRequest request) {
		String memberId = null;
		ServiceAction action = ServiceAction.LEDGER_REGISTER;

		try{
			memberId = securityUtil.getMemberId();

			Ledger ledger = createLedger(memberId, request);

			Ledger savedLedger = saveLedger(ledger);

			processImages(request, savedLedger);

			log.info("{} 성공   |   memberId={}   |   result=success", action.getTitle(), memberId);
		}catch (BusinessException e) {
			log.error("[{}] {} 실패   |   memberId={}   |   result=failure   |   errorCode={}", e.getTraceId(), action.getTitle(), memberId, e.getErrorCode());

			throw e.withService(action);
		}
	}

	private Ledger createLedger(String memberId, LedgerWriteRequest request) {
		return Ledger.create(memberId, request);
	}

	private Ledger saveLedger(Ledger ledger) {
		try{
			Long id = ledgerRepository.save(ledger);

			Ledger savedLedger = ledgerRepository.findById(id);
			if(savedLedger == null) {
				throw BusinessException.of(
						LEDGER_TARGET_NOT_FOUND,
						"가계부 정보를 불러오지 못 했습니다. 잠시 후 다시 시도해 주세요.",
						"가계부 조회 실패   |   reason=객체없음   |   object=Ledger   |   value=" + id
				);
			}

			return savedLedger;
		}catch (DuplicateKeyException e) {
			throw BusinessException.of(
					LEDGER_COLLISION_UNIQUE_CONSTRAINT,
					"가계부 등록 중 문제가 발생했습니다. 다시 시도해 주세요.",
					"가계부 저장 실패   |   reason=DB저장실패   |   detail=중복키   |   object=Ledger   |   value={memberId:" + ledger.getMemberId() + ", ledgerCode:" + ledger.getCode() + "}",
					e
			);
		}catch (DataIntegrityViolationException e) {
			throw BusinessException.of(
					LEDGER_COLLISION_UNIQUE_CONSTRAINT,
					"가계부 등록 중 문제가 발생했습니다. 다시 시도해 주세요.",
					"가계부 저장 실패   |   reason=DB저장실패   |   detail=데이터 무결성 위반   |   object=Ledger   |   value={memberId:" + ledger.getMemberId() + ", ledgerCode:" + ledger.getCode() + "}",
					e
			);
		}
	}

	private void processImages(LedgerImageRequest request, Ledger ledger) {
		if(request.hasImage()) {
			uploadImages(request.getImage(), ledger);
		}
	}

	private void uploadImages(List<MultipartFile> files, Ledger ledger) {
		List<File> successSaved = new ArrayList<>();
		List<LedgerImage> ledgerImages = new ArrayList<>();

		try{
			for(int i=0; i<files.size(); i++) {
				StoredFile savedFile = fileService.storeFile(files.get(i), ledger, ledgerImageNameStrategy);

				successSaved.add(new File(savedFile.getFullPath()));
				ledgerImages.add(LedgerImage.create(ledger.getId(), savedFile.getRelativePath(), i+1));
			}

			imageRepository.saveAll(ledgerImages);
		}catch(BusinessException e) {
			fileService.deleteFiles(successSaved);

			throw e.withUserMessage("이미지 저장 중 문제가 발생했습니다. 다시 시도해주세요.");
		}catch (DataAccessException e) {
			fileService.deleteFiles(successSaved);

			throw BusinessException.of(
					LEDGER_ETC_DB_ERROR,
					"이미지 저장 중 문제가 발생했습니다. 다시 시도해주세요.",
					"이미지 저장 실패   |   reason=DB추가실패   |   object=LedgerImage   |   value={memberId: " + ledger.getMemberId() + ", ledgerId: " + ledger.getId() + "}",
					e
			);
		}
	}

}
