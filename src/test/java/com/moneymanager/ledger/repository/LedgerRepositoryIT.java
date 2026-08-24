package com.moneymanager.ledger.repository;

import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.moneymanager.global.exception.code.ErrorCode.DATA_NOT_FOUND;
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

            Ledger ledger = LedgerFixture.builder()
                    .memberId(MemberTestData.MEMBER_ID)
                    .create();

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
            Ledger ledger = LedgerFixture.builder().id(null).saved();

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
                    .saved();

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
    @DisplayName("가계부 수정할 때")
    class Update {

        Ledger saved;

        @BeforeEach
        void setUp() {
            Long id = target.save(
                    LedgerFixture.builder()
                            .id(null)
                            .saved()
            );

            saved = target.findById(id);
        }

        @Test
        @DisplayName("가계부 번호가 있으면 가계부 정보를 수정 후 번호를 반환한다.")
        void updatesAccountBook_whenAccountBookIdExists() {
            //given
            Ledger ledger = LedgerFixture.builder()
                    .id(saved.getId())
                    .memberId(saved.getMemberId())
                    .category("020101")
                    .money(50000L, PaymentType.BANK)
                    .saved();

            //when
            Long result = target.save(ledger);

            //then
            Ledger changed = target.findById(result);

            assertThat(changed.getId()).isEqualTo(ledger.getId());
            assertThat(changed.getCode()).isEqualTo(ledger.getCode());
            assertThat(changed.getMoney()).isEqualTo(ledger.getMoney());
        }

        @Test
        @DisplayName("선택 정보가 포함되면 선택 정보도 수정한다.")
        void updatesAllFields_whenOptionalFieldsAreGiven() {
            //given
            Ledger ledger = LedgerFixture.builder()
                    .id(saved.getId())
                    .memberId(saved.getMemberId())
                    .place(LedgerTestData.PLACE_NAME, LedgerTestData.ROAD_ADDRESS, LedgerTestData.DETAIL_ADDRESS)
                    .saved();

            //when
            Long result = target.save(ledger);

            //then
            Ledger changed = target.findById(result);

            assertThat(changed.getId()).isEqualTo(ledger.getId());
            assertThat(changed.getPlace()).isEqualTo(ledger.getPlace());
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
                            .saved()
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
                    .hasErrorCode(DATA_NOT_FOUND)
                    .hasWork("가계부 번호로 가계부 조회")
                    .hasTarget(Ledger.class)
                    .hasValue("id", id);
        }

    }


    @Nested
    @DisplayName("가계부 코드로 조회할 때")
    class FindByCode {

        @Test
        @DisplayName("존재하는 코드면 해당하는 가계부를 조회한다.")
        void findsLedger_whenCodeExists() {
            //given
            Member member = saveMember();
            Long ledgerId = ledgerRepository.save(
                    LedgerFixture.builder()
                            .id(null)
                            .memberId(member.getId())
                            .saved()
            );

            String code = ledgerRepository.findById(ledgerId).getCode();

            //when
            Ledger result = target.findByCode(code);

            //then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(ledgerId);
            assertThat(result.getMemberId()).isEqualTo(member.getId());
            assertThat(result.getCode()).isEqualTo(code);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
        @DisplayName("코드가 null이거나 비어있으면 예외를 발생시킨다.")
        void throwsException_whenCodeIsNullOrBlank(String code) {
        	//when
            Throwable throwable = catchThrowable(() -> target.findByCode(code));

        	//then
        	ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(DATA_NOT_FOUND)
                    .hasWork("가계부 코드로 가계부 조회")
                    .hasTarget(Ledger.class)
                    .hasValue("code", code);
        }

        @Test
        @DisplayName("존재하지 않은 코드면 예외를 발생시킨다.")
        void throwsException_whenCodeDoesNotExist() {
            //given
            String code = "no-exist";

            //when
            Throwable throwable = catchThrowable(() -> target.findByCode(code));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(DATA_NOT_FOUND)
                    .hasWork("가계부 코드로 가계부 조회")
                    .hasTarget(Ledger.class)
                    .hasValue("code", code);
        }

    }

}