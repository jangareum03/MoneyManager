package com.moneymanager.ledger.service.application;

import com.moneymanager.ledger.domain.dto.request.LedgerUpdateRequest;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.entity.LedgerImage;
import com.moneymanager.ledger.repository.LedgerImageRepository;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerFixture;
import com.moneymanager.support.fixture.file.ImageFixture;
import com.moneymanager.support.fixture.request.LedgerUpdateRequestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.dao.DataAccessException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.application<br>
 * 파일이름       : LedgerServiceRollbackIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 24<br>
 * 설명              : LedgerServiceRollback 클래스 롤백을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 8. 24</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
public class LedgerServiceRollbackIT extends IntegrationTest {

    @Autowired
    LedgerService target;

    @SpyBean
    LedgerImageRepository imageRepository;

    @Nested
    @DisplayName("가계부 수정 요청할 때")
    class Update {

        Ledger savedLedger;

        @BeforeEach
        void setUp() throws IOException {
            Long id = ledgerRepository.save(
                    LedgerFixture.builder().memberId(MemberTestData.MEMBER_ID).create()
            );

            savedLedger = ledgerRepository.findById(id);

            deleteTempDir(getTempDir());
        }

        @Test
        @DisplayName("변경된 수정사항 저장 중 실패하면 가계부가 수정되지 않는다.")
        void throwsExceptionAndRollbacks_whenAccountBookUpdateFails() {
        	//given
            String code = savedLedger.getCode();
            LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
                    .categoryCode("01")
                    .build();
        	
        	//when
            assertThatThrownBy(() -> target.processLedgerUpdate(code, request));
        	
        	//then
            Ledger ledger = ledgerRepository.findById(savedLedger.getId());

            assertThat(ledger.getCode()).isEqualTo(code);
            assertThat(ledger.getCategory()).isNotEqualTo(request.getCategoryCode());
        }
        
        @Test
        @DisplayName("이미지 파일 저장 중 실패하면 가계부가 수정되지 않는다.")
        void throwsExceptionAndRollbacks_whenImageFileSaveFails() {
        	//given
            String code = savedLedger.getCode();
            LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
                    .memo("수정")
                    .images(
                            List.of(
                                    ImageFixture.emptyFile()
                            )
                    )
                    .build();
        	
        	//when
            assertThatThrownBy(() -> target.processLedgerUpdate(code, request));
        	
        	//then
        	Ledger ledger = ledgerRepository.findById(savedLedger.getId());

            assertThat(ledger.getMemo()).isNotEqualTo(request.getMemo());
        }
        
        @Test
        @DisplayName("이미지 정보 저장 중 실패하면  가계부 수정되지 않고 파일도 삭제된다.")
        void throwsExceptionAndDeletesFile_whenImageInfoSaveFails() throws IOException {
            //given
            String code = savedLedger.getCode();
            LedgerUpdateRequest request = LedgerUpdateRequestFixture.builder()
                    .memo("수정")
                    .images(
                            List.of(
                                    ImageFixture.jpg("test")
                            )
                    )
                    .build();

            doThrow(new DataAccessException("이미지 정보 저장 실패") {})
                    .when(imageRepository)
                            .saveAll(any());

            //when
            assertThatThrownBy(() -> target.processLedgerUpdate(code, request));

            //then
            Ledger ledger = ledgerRepository.findById(savedLedger.getId());
            assertThat(ledger.getMemo()).isNotEqualTo(request.getMemo());

            List<LedgerImage> images = imageRepository.findByLedgerId(savedLedger.getId());
            assertThat(images.size()).isEqualTo(0);

            //then: 폴더 미존재 검증
            assertThat(Files.exists(getTempDir())).isFalse();
        }

    }

}