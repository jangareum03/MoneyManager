package com.moneymanager.ledger.repository;

import com.moneymanager.global.exception.code.LedgerErrorCode;
import com.moneymanager.global.exception.exception.InternalException;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.fixture.entity.LedgerFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.repository<br>
 * 파일이름       : LedgerRepositoryIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 20<br>
 * 설명              : LedgerRepository 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 8. 20</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Transactional
class LedgerRepositoryIT extends IntegrationTest {

    @Autowired
    private LedgerRepository target;

    @Nested
    @DisplayName("가계부 저장할 때")
    class Save {

        @Test
        @DisplayName("가계부 ID가 null이면 새로운 Ledger를 저장한다.")
        void savesLedgerAndReturnsGeneratedId_whenLedgerIdIsNull() {
            //given
            Long before = target.count();

            Ledger ledger = LedgerFixture.builder().id(null).build();

            //when
            target.save(ledger);

            //then
            Long after = target.count();

            assertThat(before).isLessThan(after);
        }

        @Test
        @DisplayName("필수값만 요청하면 요청한 값이 정상적으로 저장된다.")
        void saves_whenOnlyRequiredFieldsAreGiven() {
        	//given
            Ledger ledger = LedgerFixture.builder().id(null).build();
        	
        	//when
            Long result = target.save(ledger);
        	
        	//then
            Ledger saved = target.findById(result);

            assertThat(saved)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "createdAt")
                    .isEqualTo(ledger);

            assertThat(saved.getId()).isGreaterThan(1L);
            assertThat(saved.getCreatedAt()).isNotNull();
        }
        
        @Test
        @DisplayName("선택값도 요청하면 요청한 값이 정상적으로 저장된다")
        void saves_whenOptionalFieldsAreGiven() {
            //given
            Ledger ledger = LedgerFixture.builder()
                    .id(null)
                    .memo(LedgerTestData.MEMO)
                    .place(LedgerTestData.PLACE_NAME, LedgerTestData.ROAD_ADDRESS, LedgerTestData.DETAIL_ADDRESS)
                    .build();

            //when
            Long result = target.save(ledger);

            //then
            Ledger saved = target.findById(result);

            assertThat(saved)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "createdAt")
                    .isEqualTo(ledger);

            assertThat(saved.getId()).isGreaterThan(1L);
            assertThat(saved.getCreatedAt()).isNotNull();
        }

    }


    @Nested
    @DisplayName("가계부 번호로 조회할 때")
    class FindById {

        private Long id;

        @BeforeEach
        void setUp() {
            id = target.save(
                    LedgerFixture.builder()
                            .id(null)
                            .build()
            );
        }

        @Test
        @DisplayName("가계부 번호가 존재하면 번호에 해당하는 가계부를 조회한다.")
        void returnsLedger_whenLedgerIdExists() {
        	//when
            Ledger result = target.findById(id);
        	
        	//then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(id);
        }
        
        @Test
        @DisplayName("가계부 번호가 존재하지 않으면 예외를 발생시킨다.")
        void throwsInternalException_whenLedgerIdDoesNotExist() {
            //given
            Long id = 0L;

        	//when
            Throwable throwable = catchThrowable(() -> target.findById(id));

        	//then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .isInstanceOf(InternalException.class)
                    .hasErrorCode(LedgerErrorCode.DATA_NOT_FOUND)
                    .hasWork("가계부 번호로 가계부 조회")
                    .hasTarget(Ledger.class)
                    .hasValue("id", id);
        }

    }

}