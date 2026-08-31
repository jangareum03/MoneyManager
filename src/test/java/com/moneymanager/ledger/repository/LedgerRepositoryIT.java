package com.moneymanager.ledger.repository;

import com.moneymanager.ledger.domain.dto.response.history.LedgerSearchCondition;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.entity.LedgerImage;
import com.moneymanager.ledger.domain.enums.HistoryMenu;
import com.moneymanager.ledger.domain.enums.PaymentType;
import com.moneymanager.ledger.domain.query.LedgerHistoryQuery;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static com.moneymanager.global.exception.code.ErrorCode.DATA_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Named.named;

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

    @Autowired
    LedgerImageRepository imageRepository;

    @Nested
    @DisplayName("가계부 저장할 때")
    class Save {

        @Test
        @DisplayName("가계부 ID가 null이면 새로운 Ledger를 저장한다.")
        void savesLedgerAndReturnsGeneratedId_whenLedgerIdIsNull() {
            //given
            Long before = target.count();

            Ledger ledger = LedgerTestFixture.builder()
                    .memberId(MemberTestData.DEFAULT_ID)
                    .build();

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
            Ledger ledger = LedgerTestFixture.builder().build();

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
            Ledger ledger = LedgerTestFixture.builder()
                    .withPlace()
                    .withMemo()
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
    @DisplayName("가계부 수정할 때")
    class Update {

        Ledger saved;

        @BeforeEach
        void setUp() {
            Long id = ledgerRepository.save(LedgerTestFixture.builder().build());

            saved = ledgerRepository.findById(id);
        }

        @Test
        @DisplayName("가계부 번호가 있으면 가계부 정보를 수정 후 번호를 반환한다.")
        void updatesAccountBook_whenAccountBookIdExists() {
            //given
            Ledger ledger = LedgerTestFixture.builder()
                    .category("020101")
                    .money(50000L, PaymentType.BANK)
                    .buildExisting(saved.getId(), saved.getCode());

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
            Ledger ledger = LedgerTestFixture.builder()
                    .withPlace()
                    .buildExisting(saved.getId(), saved.getCode());

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

        private Ledger saved;

        @BeforeEach
        void setUp() {
            Long id = ledgerRepository.save(LedgerTestFixture.builder().build());

            saved = ledgerRepository.findById(id);
        }

        @Test
        @DisplayName("가계부 번호가 존재하면 번호에 해당하는 가계부를 조회한다.")
        void returnsLedger_whenLedgerIdExists() {
            //when
            Ledger result = target.findById(saved.getId());

            //then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(saved.getId());
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

        private Ledger saved;

        @BeforeEach
        void setUp() {
            Long id = ledgerRepository.save(LedgerTestFixture.builder().build());

            saved = ledgerRepository.findById(id);
        }

        @Test
        @DisplayName("존재하는 코드면 해당하는 가계부를 조회한다.")
        void findsLedger_whenCodeExists() {
            //given
            String code = ledgerRepository.findById(saved.getId()).getCode();

            //when
            Ledger result = target.findByCode(code);

            //then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(saved.getId());
            assertThat(result.getMemberId()).isEqualTo(saved.getMemberId());
            assertThat(result.getCode()).isEqualTo(code);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @MethodSource("com.moneymanager.support.stream.StringTestStream#blankStrings")
        @DisplayName("코드가 null이거나 비어있으면 null을 반환한다.")
        void returnNull_whenCodeIsNullOrBlank(String code) {
        	//when
            Ledger result = target.findByCode(code);

        	//then
        	assertThat(result).isNull();
        }

        @Test
        @DisplayName("존재하지 않은 코드면 null을 반환한다.")
        void returnNull_whenCodeDoesNotExist() {
            //given
            String code = "no-exist";

            //when
            Ledger result = target.findByCode(code);

            //then
            assertThat(result).isNull();
        }

    }


    @Nested
    @DisplayName("가계부 코드 여러개로 조회할 때")
    class FindByCodes {
        
        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null이거나 비어있으면 빈 리스트를 반환한다.")
        void findsEmptyList_whenCodesAreNullOrEmpty(List<String> codes) {
        	//when
            List<Ledger> result = target.findByCodeIn(codes);
        	
        	//then
        	assertThat(result).isEmpty();
        }
        
        @Test
        @Sql("/sql/ledger-get-test.sql")
        @DisplayName("존재하는 코드면 리스트에 포함하여 반환한다.")
        void findsCodes_whenCodesExist() {
        	//given
            List<String> codes = List.of("code-1", "code-3");
        	
        	//when
            List<Ledger> result = target.findByCodeIn(codes);
        	
        	//then
        	assertThat(result)
                    .hasSize(codes.size())
                    .extracting(Ledger::getCode)
                    .contains("code-1", "code-3");
        }
        
        @Test
        @Sql("/sql/ledger-get-test.sql")
        @DisplayName("존재하지 않은 코드는 제외 후 반환한다.")
        void findsCodesExcludingNonExisting_whenSomeCodesDoesNotExist() {
            //given
            List<String> codes = List.of("code-1", "no-exit", "code-3");

            //when
            List<Ledger> result = target.findByCodeIn(codes);

            //then
            assertThat(result)
                    .hasSize(2)
                    .extracting(Ledger::getCode)
                    .contains("code-1", "code-3");
        }

    }


    @Nested
    @Sql("/sql/ledger-history-test.sql")
    @DisplayName("가계부 거래날짜로 조회할 때")
    class FindByTransactionDate {
        
        @Test
        @DisplayName("시작일과 종료일 내에 작성한 가계부가 있으면 조회한다.")
        void findsAccountBooks_whenDatesAreInRange() {
        	//given
            String memberId = "member1";
            LocalDate fromDate = LocalDate.of(2026, 1, 1);
            LocalDate toDate = LocalDate.of(2026, 1, 3);
        	
        	//when
            List<LedgerHistoryQuery> result = target.findByTransactionDateBetween(memberId, fromDate, toDate);
        	
        	//then
        	assertThat(result).hasSize(4);
            assertThat(result)
                    .extracting(LedgerHistoryQuery::getCode)
                    .contains("code3", "code4", "code5", "code6");
        }
        
        @Test
        @DisplayName("시작일에 작성한 가계부가 있으면 조회한다.")
        void findsAccountBooks_whenFromDateIsGiven() {
        	//given
            String memberId = "member1";
            LocalDate fromDate = LocalDate.of(2026, 1, 1);
            LocalDate toDate = LocalDate.of(2026, 1, 2);

            //when
            List<LedgerHistoryQuery> result = target.findByTransactionDateBetween(memberId, fromDate, toDate);

            //then
            assertThat(result).hasSize(1);
            assertThat(result)
                    .extracting(LedgerHistoryQuery::getCode)
                    .containsOnly("code3");
        }
        
        @Test
        @DisplayName("종료일에 작성한 가계부가 있으면 조회한다.")
        void findsAccountBooks_whentoDateIsGiven() {
            //given
            String memberId = "member1";
            LocalDate fromDate = LocalDate.of(2026, 1, 2);
            LocalDate toDate = LocalDate.of(2026, 1, 3);

            //when
            List<LedgerHistoryQuery> result = target.findByTransactionDateBetween(memberId, fromDate, toDate);

            //then
            assertThat(result).hasSize(3);
            assertThat(result)
                    .extracting(LedgerHistoryQuery::getCode)
                    .containsOnly("code4", "code5", "code6");
        }
        
        @Test
        @DisplayName("시작일과 종료일 내에 작성한 가계부가 없으면 빈 리스트를 조회한다.")
        void returnsEmptyList_whenAccountBooksDoNotExistInRange() {
            //given
            String memberId = "member1";
            LocalDate fromDate = LocalDate.of(2026, 1, 6);
            LocalDate toDate = LocalDate.of(2026, 1, 10);

            //when
            List<LedgerHistoryQuery> result = target.findByTransactionDateBetween(memberId, fromDate, toDate);

            //then
            assertThat(result).isEmpty();
        }
        
        @Test
        @DisplayName("회원번호 외에 다른 회원의 가계부는 조회되지 않는다.")
        void findsOnlyUserAccountBooks_whenUserIdIsGiven() {
            //given
            String memberId = "member1";
            LocalDate fromDate = LocalDate.of(2026, 1, 2);
            LocalDate toDate = LocalDate.of(2026, 1, 3);

            //when
            List<LedgerHistoryQuery> result = target.findByTransactionDateBetween(memberId, fromDate, toDate);

            //then
            assertThat(result).hasSize(3);
            assertThat(result)
                    .extracting(LedgerHistoryQuery::getCode)
                    .contains("code4", "code5", "code6");
        }
        
        @Test
        @DisplayName("조회된 가계부는 거래날짜 내림차순으로 정렬된다.")
        void sortsAccountBooksByTransactionDateDesc_whenAccountBooksExist() {
            //given
            String memberId = "member1";
            LocalDate fromDate = LocalDate.of(2026, 1, 4);
            LocalDate toDate = LocalDate.of(2026, 1, 5);

            //when
            List<LedgerHistoryQuery> result = target.findByTransactionDateBetween(memberId, fromDate, toDate);

            //then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(LedgerHistoryQuery::getCode)
                    .containsExactly("code8", "code7");
        }
        
        @Test
        @DisplayName("조회된 가계부의 거래날짜가 동일하면 등록일 내림차순으로 정렬된다.")
        void sortsAccountBooksByCreatedDateDesc_whenTransactionDatesAreEqual() {
            //given
            String memberId = "member1";
            LocalDate fromDate = LocalDate.of(2026, 1, 1);
            LocalDate toDate = LocalDate.of(2026, 1, 3);

            //when
            List<LedgerHistoryQuery> result = target.findByTransactionDateBetween(memberId, fromDate, toDate);

            //then
            assertThat(result).hasSize(4);
            assertThat(result)
                    .extracting(LedgerHistoryQuery::getCode)
                    .containsExactly("code6", "code5", "code4", "code3");
        }

    }


    @Nested
    @Sql("/sql/ledger-history-test.sql")
    @DisplayName("가계부 검색 조건으로 조회할 때")
    class FindBySearch {

        String memberId = "member1";
        LocalDate fromDate = LocalDate.of(2026, 1, 1);
        LocalDate toDate = LocalDate.of(2026, 1, 3);
        
        @ParameterizedTest
        @EnumSource(
                value = HistoryMenu.class,
                names = {"ALL", "PERIOD"}
        )
        @DisplayName("전체(ALL) 및 기간(PERIOD) 검색이면 기간 내에 가계부 내역만 조회한다.")
        void returnsAccountBookEntries_whenTypeIsAllOrPeriod(HistoryMenu menu) {
        	//given
            LedgerSearchCondition searchCondition = LedgerSearchCondition.of(menu);
        	
        	//when
            List<LedgerHistoryQuery> result = target.findAllByConditionAndDateRange(memberId, fromDate, toDate, searchCondition);
        	
        	//then
        	assertThat(result).hasSize(4);
        }
        
        @Test
        @DisplayName("수입(CATEGORY) 검색이면 기간 내에 수입 가계부 내역만 조회한다.")
        void returnsIncomeEntries_whenTypeIsCategoryAndIncome() {
            //given
            LedgerSearchCondition searchCondition = LedgerSearchCondition.ofKeyword(HistoryMenu.CATEGORY, "010000");

            //when
            List<LedgerHistoryQuery> result = target.findAllByConditionAndDateRange(memberId, fromDate, toDate, searchCondition);

            //then
            assertThat(result).hasSize(2);
        }
        
        @Test
        @DisplayName("지출(CATEGORY) 검색이면 기간 내에 지출 가계부 내역만 조회한다.")
        void returnsExpenseEntries_whenTypeIsCategoryAndExpense() {
            //given
            LedgerSearchCondition searchCondition = LedgerSearchCondition.ofKeyword(HistoryMenu.CATEGORY, "020000");

            //when
            List<LedgerHistoryQuery> result = target.findAllByConditionAndDateRange(memberId, fromDate, toDate, searchCondition);

            //then
            assertThat(result).hasSize(2);
        }
        
        @ParameterizedTest
        @MethodSource("validCategories")
        @DisplayName("카테고리(SUB_CATEGORY) 검색이면 기간 내에 카테고리가 포함된 가계부 내역만 조회한다.")
        void returnsEntries_whenSubCategoryIsGiven(List<String> keywords, int expected) {
            //given
            LedgerSearchCondition searchCondition = LedgerSearchCondition.ofKeywords(HistoryMenu.SUB_CATEGORY, keywords);

            //when
            List<LedgerHistoryQuery> result = target.findAllByConditionAndDateRange(memberId, fromDate, toDate, searchCondition);

            //then
            assertThat(result).hasSize(expected);
        }

        static Stream<Arguments> validCategories() {
            return Stream.of(
                    Arguments.of(
                            named("카테고리 1개인 경우 (코드: 020202)", List.of("020202")),
                            1
                    ),
                    Arguments.of(
                            named("카테고리 2개인 경우 (코드: 010101, 020301)", List.of("010101", "020301")),
                            3
                    )
            );
        }
        
        @Test
        @DisplayName("메모(MEMO) 검색이면 기간 내에 메모 내용이 포함된 가계부 내역만 조회한다.")
        void returnsEntries_whenMemoIsGiven() {
            //given
            LedgerSearchCondition searchCondition = LedgerSearchCondition.ofKeyword(HistoryMenu.MEMO, "이");

            //when
            List<LedgerHistoryQuery> result = target.findAllByConditionAndDateRange(memberId, fromDate, toDate, searchCondition);

            //then
            assertThat(result).hasSize(2);
        }

    }


    @Nested
    @Sql("/sql/ledger-delete-test.sql")
    @DisplayName("가계부 코드 여러개로 삭제할 때")
    class DeleteByCodes {
        
        @Test
        @DisplayName("요청하는 코드가 비어있으면 0개를 반환한다.")
        void deletesNothing_whenCodeListIsEmpty() {
        	//given
            List<Long> ids = List.of();
        	
        	//when
            int result = target.deleteByIdIn(ids);
        	
        	//then
        	assertThat(result).isZero();
        }
        
        @Test
        @DisplayName("존재하는 가계부 코드면 삭제 후 삭제된 개수를 반환한다.")
        void deletesHouseholdLedgerAndReturnsCount_whenCodeExists() {
            //given
            List<Long> ids = List.of(1L, 2L);

            //when
            int result = target.deleteByIdIn(ids);

            //then
            assertThat(result).isEqualTo(ids.size());
        }
        
        @Test
        @DisplayName("존재하지 않은 가계부 코드면 삭제된 개수에 포함하지 않는다.")
        void deletesNothing_whenCodeDoesNotExist() {
            //given
            List<Long> ids = List.of(1L, 2L, 999L);

            //when
            int result = target.deleteByIdIn(ids);

            //then
            assertThat(result).isEqualTo(2);
        }
        
        @Test
        @DisplayName("삭제된 가계부에 대한 이미지 정보 같이 삭제된다.")
        void deletesImages_whenHouseholdLedgerIsDeleted() {
            //given
            List<Long> ids = List.of(1L, 2L);

            List<LedgerImage> beforeImages = imageRepository.findByLedgerId(1L);
            assertThat(beforeImages).hasSize(1);

            //when
            int result = target.deleteByIdIn(ids);

            //then
            assertThat(result).isEqualTo(ids.size());

            List<LedgerImage> afterImages = imageRepository.findByLedgerId(1L);
            assertThat(afterImages).hasSize(0);
        }

    }

}