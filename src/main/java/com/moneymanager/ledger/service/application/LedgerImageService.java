package com.moneymanager.ledger.service.application;

import com.moneymanager.global.domain.FileMetadata;
import com.moneymanager.global.exception.code.ErrorCode;
import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.log.LogContent;
import com.moneymanager.ledger.domain.dto.response.ImageSlot;
import com.moneymanager.ledger.domain.entity.LedgerImage;
import com.moneymanager.ledger.domain.enums.SlotStatus;
import com.moneymanager.ledger.repository.LedgerImageRepository;
import com.moneymanager.ledger.service.policy.LedgerPolicy;
import com.moneymanager.ledger.service.storage.LedgerImageStorage;
import com.moneymanager.member.service.read.MemberReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.moneymanager.global.exception.code.ErrorCode.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.application<br>
 * 파일이름       : LedgerImageService<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 21<br>
 * 설명              : 가계부 이미지 로직 흐름을 관리하는 클래스
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
 * 		 	  <td>26. 8. 21</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
@RequiredArgsConstructor
public class LedgerImageService {

    private final MemberReadService memberReadService;
    private final LedgerImageRepository imageRepository;

    private final LedgerPolicy ledgerPolicy;
    private final LedgerImageStorage imageStorage;

    void processImageUpload(String memberId, Long ledgerId, List<MultipartFile> images) {
        int availImgCnt = memberReadService.getAvailableImageCount(memberId);

        List<SlotStatus> slots = ledgerPolicy.imageSlots(availImgCnt)
                .stream()
                .map(ImageSlot::getStatus)
                .toList();

        int index = 0;
        List<FileMetadata> metadataList = new ArrayList<>();

        try{
            for (; index < images.size(); index++) {
                switch (slots.get(index)) {
                    case LOCKED:
                        continue;
                    case FILLED:
                    case EMPTY:
                        metadataList.add(imageStorage.store(images.get(index), memberId));
                }
            }

            //이미지 정보 저장
            List<LedgerImage> ledgerImages = createLedgerImage(ledgerId, metadataList);
            imageRepository.saveAll(ledgerImages);
        }catch (IOException e) {
            cleanFiles(metadataList);

            throwException(
                    FILE_UPLOAD_FAILED,
                    "이미지 파일 저장",
                    FileMetadata.class,
                    e,
                    "memberId", memberId,
                    "originalFilename", images.get(index).getOriginalFilename()
            );
        }catch (DataAccessException e) {
            cleanFiles(metadataList);

            throwException(
                    INTERVAL_SERVER_ERROR,
                    "가계부 이미지 정보 저장",
                    LedgerImage.class,
                    e,
                    "memberId", memberId,
                    "ledgerId", ledgerId,
                    "imageCount", images.size()
            );
        }
    }


    //===== processImageUpload 보조 메서드 =====
    private List<LedgerImage> createLedgerImage(Long ledgerId, List<FileMetadata> metadata) {
        return IntStream.range(0, metadata.size())
                .mapToObj(i -> LedgerImage.of(
                        ledgerId,
                        metadata.get(i).getRelativePath(),
                        i + 1
                ))
                .toList();
    }


    //===== 유틸 메서ㅓ드 =====
    private void cleanFiles(List<FileMetadata> metadataList) {
        for(FileMetadata fileMetadata : metadataList){
            Path path = fileMetadata.getAbsolutePath();

            imageStorage.delete(path);
        };
    }

    private void throwException(ErrorCode errorCode, String work, Class target, Throwable e, Object... values) {
        throw new ApplicationException(
                errorCode,
                LogContent.of(
                        work,
                        target,
                        values
                ),
                e
        );
    }

}