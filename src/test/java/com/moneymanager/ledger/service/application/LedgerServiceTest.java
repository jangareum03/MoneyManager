package com.moneymanager.ledger.service.application;

import com.moneymanager.global.domain.enums.DatePatterns;
import com.moneymanager.global.exception.exception.ApplicationException;
import com.moneymanager.global.security.CurrentUser;
import com.moneymanager.global.util.date.DateTimeUtil;
import com.moneymanager.ledger.domain.dto.request.LedgerWriteRequest;
import com.moneymanager.ledger.domain.dto.response.ImageSlot;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep1Response;
import com.moneymanager.ledger.domain.dto.response.LedgerWriteStep2Response;
import com.moneymanager.ledger.domain.dto.response.item.CategoryItem;
import com.moneymanager.ledger.domain.dto.response.item.FixedTypeItem;
import com.moneymanager.ledger.domain.dto.response.item.LedgerTypeItem;
import com.moneymanager.ledger.domain.dto.response.item.PaymentTypeItem;
import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.enums.CategoryType;
import com.moneymanager.ledger.domain.enums.DateUnit;
import com.moneymanager.ledger.domain.enums.FixedType;
import com.moneymanager.ledger.domain.enums.SlotStatus;
import com.moneymanager.ledger.service.command.LedgerCommandService;
import com.moneymanager.ledger.service.policy.LedgerPolicy;
import com.moneymanager.ledger.service.read.CategoryReadService;
import com.moneymanager.ledger.service.validation.LedgerRegisterValidator;
import com.moneymanager.member.service.read.MemberReadService;
import com.moneymanager.support.data.CategoryTestData;
import com.moneymanager.support.data.LedgerTestData;
import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerFixture;
import com.moneymanager.support.fixture.entity.category.IncomeCategoryFixture;
import com.moneymanager.support.fixture.entity.category.OutlayCategoryFixture;
import com.moneymanager.support.fixture.request.LedgerWriteRequestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.application<br>
 * 파일이름       : LedgerServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 13<br>
 * 설명              : LedgerService 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 8. 13</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class LedgerServiceTest {

    @InjectMocks
    private LedgerService target;

    @Mock
    private CurrentUser currentUser;

    @Mock
    private LedgerImageService imageService;

    @Mock
    private CategoryReadService categoryReadService;

    @Mock
    private MemberReadService memberReadService;

    @Mock
    private LedgerCommandService commandService;

    @Mock
    private LedgerRegisterValidator registerValidator;

    @Mock
    private LedgerPolicy ledgerPolicy;


    @Nested
    @DisplayName("작성 1단계에 필요한 정보를 조회할 때")
    class GetWriteStep1Data {

        @Test
        @DisplayName("현재 날짜로 올바른 연, 월, 일 리스트가 Response에 포함된다.")
        void returnsDateList_whenCurrentDateIsGiven() {
            //given: 리스트 생성 기준이 되는 최소 날짜와 최대날짜를 20200101, 20250205로 설정한다.
            when(ledgerPolicy.minimumDate())
                    .thenReturn(LocalDate.of(2020, 1, 1));

            when(ledgerPolicy.maximumDate())
                    .thenReturn(LocalDate.of(2025, 2, 5));

            //when
            LedgerWriteStep1Response result = target.getStep1();

            //then: 날짜 기준으로 리스트가 포함된다.
            assertThat(result.getYears())
                    .containsExactly(2020, 2021, 2022, 2023, 2024, 2025);

            assertThat(result.getMonths())
                    .containsExactly(1, 2);

            assertThat(result.getDays())
                    .containsExactly(1, 2, 3, 4, 5);

            //then: 기본 설정된 값으로 생성된다.
            LocalDate today = LocalDate.now();

            assertThat(result.getCurrentYear()).isEqualTo(today.getYear());
            assertThat(result.getCurrentMonth()).isEqualTo(today.getMonthValue());
            assertThat(result.getCurrentDay()).isEqualTo(today.getDayOfMonth());

            assertThat(result.getTypes())
                    .hasSize(2)
                    .extracting(LedgerTypeItem::getValue)
                    .containsExactly("income", "outlay");

            assertThat(result.getDisplayDate()).isEqualTo(
                    DateTimeUtil.formatDate(today, DatePatterns.KOREAN_DATE_WITH_DAY.getPattern())
            );
        }

    }


    @Nested
    @DisplayName("작성 2단계에 필요한 정보를 조회할 때")
    class GetWriteStep2Data {

        @Nested
        @DisplayName("성공")
        class Success {

            @BeforeEach
            void setUp() {
                when(currentUser.getMemberId())
                        .thenReturn(MemberTestData.MEMBER_ID);
            }

            @Test
            @DisplayName("가계부 유형이 INCOME이면 수입 카테고리를 포함한 응답 객체가 반환된다.")
            void returnsResponseWithIncomeCategory_whenTypeIsIncome() {
                //given
                CategoryType type = CategoryType.INCOME;
                String date = "20260115";

                when(categoryReadService.getMiddleCategories(type))
                        .thenReturn(CategoryItem.from(IncomeCategoryFixture.createMiddleAll()));

                when(memberReadService.getAvailableImageCount(eq(MemberTestData.MEMBER_ID)))
                        .thenReturn(1);

                when(ledgerPolicy.imageSlots(1))
                        .thenReturn(
                                List.of(
                                        ImageSlot.ofEmptySlot(),
                                        ImageSlot.ofLockedSlot(),
                                        ImageSlot.ofLockedSlot()
                                )
                        );

                //when
                LedgerWriteStep2Response result = target.getStep2("income", date);

                //then
                assertThat(result).isNotNull();

                assertThat(result.getTitle()).isEqualTo("2026년 01월 15일 목요일");
                assertThat(result.getType()).isEqualTo(type);

                assertThat(result.getFixed())
                        .hasSize(2)
                        .extracting(FixedTypeItem::getValue)
                        .containsExactly("y", "n");

                assertThat(result.getCategories())
                        .hasSize(2)
                        .extracting(CategoryItem::getCode)
                        .containsExactly("010100", "010200");

                assertThat(result.getPaymentTypes())
                        .hasSize(4)
                        .extracting(PaymentTypeItem::getValue)
                        .containsExactly("none", "cash", "card", "bank");

                assertThat(result.getImageSlot())
                        .hasSize(3)
                        .extracting(ImageSlot::getStatus)
                        .containsExactly(SlotStatus.EMPTY, SlotStatus.LOCKED, SlotStatus.LOCKED);
            }

            @Test
            @DisplayName("가계부 유형이 OUTLAY이면 지출 카테고리를 포함한 응답 객체가 반환된다.")
            void returnsResponseWithOutlayCategory_whenTypeIsOutlay() {
                //given
                CategoryType type = CategoryType.OUTLAY;
                String date = "20260115";

                when(categoryReadService.getMiddleCategories(type))
                        .thenReturn(CategoryItem.from(OutlayCategoryFixture.createMiddleAll()));

                when(memberReadService.getAvailableImageCount(eq(MemberTestData.MEMBER_ID)))
                        .thenReturn(1);

                when(ledgerPolicy.imageSlots(1))
                        .thenReturn(
                                List.of(
                                        ImageSlot.ofEmptySlot(),
                                        ImageSlot.ofLockedSlot(),
                                        ImageSlot.ofLockedSlot()
                                )
                        );

                //when
                LedgerWriteStep2Response result = target.getStep2("outlay", date);

                //then: 지출 카테고리 목록이 포함된다.
                assertThat(result.getCategories())
                        .hasSize(3)
                        .extracting(CategoryItem::getCode)
                        .containsExactly("020100", "020200", "020300");

            }

            @Test
            @DisplayName("가계부 유형이 유효하지 않으면 INCOME으로 대체하여 진행된다.")
            void returnsResponseWithIncomeCategory_whenTypeIsInvalid() {
                //given
                String type = "none";
                String date = "20261001";

                when(categoryReadService.getMiddleCategories(eq(CategoryType.INCOME)))
                        .thenReturn(CategoryItem.from(IncomeCategoryFixture.createMiddleAll()));

                when(memberReadService.getAvailableImageCount(eq(MemberTestData.MEMBER_ID)))
                        .thenReturn(1);

                when(ledgerPolicy.imageSlots(1))
                        .thenReturn(
                                List.of(
                                        ImageSlot.ofEmptySlot(),
                                        ImageSlot.ofLockedSlot(),
                                        ImageSlot.ofLockedSlot()
                                )
                        );

                //when
                LedgerWriteStep2Response result = target.getStep2(type, date);

                //then: 수입 카테고리 목록이 포함된다.
                assertThat(result.getCategories())
                        .hasSize(2)
                        .extracting(CategoryItem::getCode)
                        .containsExactly("010100", "010200");
            }

            @ParameterizedTest
            @NullAndEmptySource
            @MethodSource("com.moneymanager.support.data.StringTestData#blankStrings")
            @DisplayName("가게부 날짜가 없으면 오늘 날짜로 대체하여 진행된다.")
            void fallbacksToToday_whenDateIsNull(String date) {
                //when
                LedgerWriteStep2Response result = target.getStep2("income", date);

                //then: 오늘 날짜로 대체된다.
                LocalDate today = LocalDate.now();

                assertThat(result.getTitle())
                        .isEqualTo(
                                DateTimeUtil.formatDate(
                                        today,
                                        DatePatterns.KOREAN_DATE_WITH_DAY.getPattern()
                                )
                        );
            }

            @Test
            @DisplayName("가계부 날짜가 유효하지 않으면 오늘 날짜로 대체하여 진행된다.")
            void fallbacksToToday_whenDateIsInvalid() {
                //given
                String date = "202601011";

                //when
                LedgerWriteStep2Response result = target.getStep2("outlay", date);

                //then: 오늘 날짜로 대체된다.
                LocalDate today = LocalDate.now();

                assertThat(result.getTitle())
                        .isEqualTo(
                                DateTimeUtil.formatDate(
                                        today,
                                        DatePatterns.KOREAN_DATE_WITH_DAY.getPattern()
                                )
                        );
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {


            @Test
            @DisplayName("카테고리가 존재하지 않으면 InternalException 예외를 전파한다.")
            void throwsInternalException_whenCategoryDoesNotExist() {
                //given
                when(categoryReadService.getMiddleCategories(any(CategoryType.class)))
                        .thenThrow(ApplicationException.class);

                //when & then
                assertThatThrownBy(() -> target.getStep2("income", "20260115"));
            }

            @Test
            @DisplayName("회원이 존재하지 않으면 InternalException 예외가 전파된다.")
            void throwsInternalException_whenUserDoesNotExist() {
                //given:
                when(currentUser.getMemberId())
                        .thenReturn("nonExistent");

                when(memberReadService.getAvailableImageCount("nonExistent"))
                        .thenThrow(ApplicationException.class);

                //when & then
                assertThatThrownBy(() -> target.getStep2("outlay", "20260115"));
            }

        }

    }


    @Nested
    @DisplayName("날짜 단위별 날짜 목록을 조회할 때")
    class FetchDateOptions {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("YEAR 단위의 유효한 날짜면 해당 연도의 월 리스트를 반환한다.")
            void returnsMonths_whenDateUnitIsYearAndDateIsValid() {
                //given
                String unit = "year";
                String date = "2026";

                List<Integer> expected = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

                when(ledgerPolicy.dateOptions(eq(DateUnit.YEAR), eq(date)))
                        .thenReturn(expected);

                //when
                List<Integer> result = target.fetchDateOptionsByUnit(unit, date);

                //then
                assertThat(result).isEqualTo(expected);

                verify(ledgerPolicy).dateOptions(eq(DateUnit.YEAR), any());
            }

            @Test
            @DisplayName("MONTH 단위의 유효한 날짜면 해당 일의 일 리스트를 반환한다.")
            void returnsDays_whenDateUnitIsMonthAndDateIsValid() {
                //given
                String unit = "month";
                String date = "202510";

                List<Integer> expected = List.of(1, 2, 3, 4, 5);

                when(ledgerPolicy.dateOptions(eq(DateUnit.MONTH), eq(date)))
                        .thenReturn(expected);

                //when
                List<Integer> result = target.fetchDateOptionsByUnit(unit, date);

                //then
                assertThat(result).isEqualTo(expected);

                verify(ledgerPolicy).dateOptions(eq(DateUnit.MONTH), any());
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("DateUnit 변환에 실패하면 예외를 전파하고 그 후 동작을 수행하지 않는다.")
            void throwsNoSuchElementException_whenDateUnitConversionFails() {
                //given
                String unit = "day";
                String date = "202510";

                //when & then
                assertThatThrownBy(() -> target.fetchDateOptionsByUnit(unit, date));

                //then
                verify(ledgerPolicy, never()).dateOptions(any(), any());
            }

            @Test
            @DisplayName("날짜 검증에 실패하면 예외를 전파하고 그 후 동작을 수행하지 않는다.")
            void throwsException_whenDateValidationFails() {
                //given
                String unit = "month";
                String date = "20250101";

                //when & then
                assertThatThrownBy(() -> target.fetchDateOptionsByUnit(unit, date));

                //then
                verify(ledgerPolicy, never()).dateOptions(any(), any());
            }

            @Test
            @DisplayName("옵션 조회 중 예외가 발생하면 해당 예외를 전파한다.")
            void throwsException_whenOptionFetchFails() {
                //given
                String unit = "month";
                String date = "202501";

                when(ledgerPolicy.dateOptions(DateUnit.MONTH, date))
                        .thenThrow(ApplicationException.class);

                //when & then
                assertThatThrownBy(() -> target.fetchDateOptionsByUnit(unit, date));

                //then
                verify(ledgerPolicy).dateOptions(any(), any());
            }

        }

    }


    @Nested
    @DisplayName("가계부를 등록할 때")
    class Register {

        private final String memberId = MemberTestData.MEMBER_ID;

        private final LedgerWriteRequest request = LedgerWriteRequestFixture.builder().build();

        private final Ledger ledger =
                LedgerFixture.builder()
                        .date(LedgerTestData.LOCAL_DATE)
                        .code(CategoryTestData.SALARY_CODE)
                        .fix(FixedType.VARIABLE)
                        .money(LedgerTestData.AMOUNT, LedgerTestData.PAYMENT_TYPE)
                        .build();

        @Nested
        @DisplayName("성공")
        class Success {

            @BeforeEach
            void setUp() {
                when(currentUser.getMemberId())
                        .thenReturn(memberId);
            }

            @Test
            @DisplayName("유효한 요청이면 정상적으로 가계부를 저장한다.")
            void savesLedger_whenRequestIsValid() {
                //given
                when(commandService.create(memberId, request))
                        .thenReturn(ledger);

                //when
                target.processLedgerRegistration(request);

                //then
                verify(registerValidator).validate(request);
                verify(commandService).create(memberId, request);
                verify(ledgerPolicy).validateCreatable(ledger);
                verify(commandService).save(ledger);

                verify(imageService, never()).processImageUpload(any(), any(), any());
            }

            @Test
            @DisplayName("이미지가 있는 요청이면 파일 저장과 함께 정보도 저장한다.")
            void savesLedgerWithImage_whenRequestContainsImage() {
                LedgerWriteRequest request =
                        LedgerWriteRequestFixture.withImages(1).build();

                when(commandService.create(memberId, request))
                        .thenReturn(ledger);

                when(commandService.save(ledger))
                        .thenReturn(ledger.getId());

                //when
                target.processLedgerRegistration(request);

                //then
                verify(registerValidator).validate(request);
                verify(commandService).create(memberId, request);
                verify(ledgerPolicy).validateCreatable(ledger);
                verify(commandService).save(ledger);
                verify(imageService).processImageUpload(memberId, ledger.getId(), request.getImages());
            }

        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("요청 검증에 실패하면 그 후 동작은 수행하지 않는다.")
            void throwsExceptionAndAbortsProcess_whenRequestValidationFails() {
                //given
                doThrow(ApplicationException.class)
                        .when(registerValidator).validate(null);

                //when
                assertThatThrownBy(() -> target.processLedgerRegistration(null));

                //then
                verify(currentUser).getMemberId();
                verify(registerValidator).validate(null);

                verify(commandService, never()).create(anyString(), any(LedgerWriteRequest.class));
            }

            @Test
            @DisplayName("가계부 생성에 실패하면 그 후 동작은 수행하지 않는다.")
            void throwsExceptionAndAbortsProcess_whenLedgerCreationFailed() {
                //given
                when(currentUser.getMemberId())
                        .thenReturn(MemberTestData.MEMBER_ID);

                when(commandService.create(memberId, request))
                        .thenThrow(ApplicationException.class);

                //when
                assertThatThrownBy(() -> target.processLedgerRegistration(request));

                //then
                verify(currentUser).getMemberId();
                verify(registerValidator).validate(request);
                verify(commandService).create(memberId, request);

                verify(ledgerPolicy, never()).validateCreatable(any(Ledger.class));
            }

            @Test
            @DisplayName("가계부 비즈니스 검증에 실패하면 그 후 동작은 수행하지 않는다.")
            void throwsExceptionAndAbortsProcess_whenBusinessValidationFails() {
                //given
                when(currentUser.getMemberId())
                        .thenReturn(MemberTestData.MEMBER_ID);

                when(commandService.create(memberId, request))
                        .thenReturn(ledger);

               doThrow(ApplicationException.class)
                       .when(ledgerPolicy).validateCreatable(ledger);

                //when
                assertThatThrownBy(() -> target.processLedgerRegistration(request));

                //then
                verify(currentUser).getMemberId();
                verify(registerValidator).validate(request);
                verify(commandService).create(memberId, request);
                verify(ledgerPolicy).validateCreatable(ledger);

                verify(commandService, never()).save(ledger);
            }

            @Test
            @DisplayName("가계부 정보 저장에 실패하면 그 후 동작은 수행하지 않는다.")
            void throwsExceptionAndAbortsProcess_whenLedgerSaveFails() {
                //given
                Ledger ledger =
                        LedgerFixture.builder()
                        .date(LedgerTestData.LOCAL_DATE)
                        .code(CategoryTestData.SALARY_CODE)
                        .fix(FixedType.VARIABLE)
                        .money(LedgerTestData.AMOUNT, LedgerTestData.PAYMENT_TYPE)
                        .build();

                when(currentUser.getMemberId())
                        .thenReturn(MemberTestData.MEMBER_ID);

                when(commandService.create(memberId, request))
                        .thenReturn(ledger);

                doThrow(new DataAccessException("DB 오류") {})
                        .when(commandService).save(ledger);

                //when
                assertThatThrownBy(() -> target.processLedgerRegistration(request))
                        ;

                //then
                verify(currentUser).getMemberId();
                verify(registerValidator).validate(request);
                verify(commandService).create(memberId, request);
                verify(ledgerPolicy).validateCreatable(ledger);
                verify(commandService).save(ledger);

                verify(imageService, never()).processImageUpload(memberId, ledger.getId(), request.getImages());
            }

        }

    }

}