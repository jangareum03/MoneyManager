package com.moneymanager.ledger.domain.dto.vo;

import com.moneymanager.support.ApplicationExceptionAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.moneymanager.global.exception.code.ErrorCode.OUT_OF_RANGE;
import static com.moneymanager.global.exception.code.ErrorCode.REQUIRED_VALUE;
import static org.assertj.core.api.Assertions.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.domain.dto.vo<br>
 * 파일이름       : LedgerPeriodTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 28<br>
 * 설명              : LedgerPeriod 클래스 기능을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 8. 28</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@DisplayName("LedgerPeriod 객체를 생성할 때")
class LedgerPeriodTest {

    @Nested
    @DisplayName("성공")
    class Success {

        @Test
        @DisplayName("시작일이 종료일보다 과거면 생성한다.")
        void creates_whenStartDateIsBeforeEndDate() {
        	//given
            LocalDate fromDate = LocalDate.of(2026, 1, 1);
            LocalDate toDate = LocalDate.of(2026, 1, 2);
        	
        	//when
            LedgerPeriod result = LedgerPeriod.of(fromDate, toDate);
        	
        	//then
        	assertThat(result.getFromDate()).isEqualTo(fromDate);
        	assertThat(result.getToDate()).isEqualTo(toDate);
        }
        
        @Test
        @DisplayName("시작일과 종료일이 같으면 생성한다.")
        void creates_whenStartDateEqualsEndDate() {
            //given
            LocalDate fromDate = LocalDate.of(2026, 1, 1);
            LocalDate toDate = LocalDate.of(2026, 1, 1);

            //when
            LedgerPeriod result = LedgerPeriod.of(fromDate, toDate);

            //then
            assertThat(result.getFromDate()).isEqualTo(fromDate);
            assertThat(result.getToDate()).isEqualTo(toDate);
        }

    }

    @Nested
    @DisplayName("실패")
    class Failure {

        @Test
        @DisplayName("시작일이 null이면 예외를 발생시킨다.")
        void throwsException_whenStartDateIsNull() {
            //given
            LocalDate toDate = LocalDate.of(2026, 1, 1);

            //when
            Throwable throwable = catchThrowable(() -> LedgerPeriod.of(null, toDate));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasWork("LedgerPeriod 생성")
                    .hasTarget(LedgerPeriod.class)
                    .hasValue("fromDate", null, "toDate", toDate);
        }
        
        @Test
        @DisplayName("종료일이 null이면 예외를 발생시킨다.")
        void throwsException_whenEndDateIsNull() {
            //given
            LocalDate fromDate = LocalDate.of(2026, 1, 1);

            //when
            Throwable throwable = catchThrowable(() -> LedgerPeriod.of(fromDate, null));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(REQUIRED_VALUE)
                    .hasWork("LedgerPeriod 생성")
                    .hasCauseMessage("fromDate와 toDate 모두 null 불가")
                    .hasTarget(LedgerPeriod.class)
                    .hasValue("fromDate", fromDate, "toDate", null);
        }
        
        @Test
        @DisplayName("시작일이 종료일보다 미래면 예외를 발생시킨다.")
        void throwsException_whenStartDateIsAfterEndDate() {
            //given
            LocalDate fromDate = LocalDate.of(2026, 1, 2);
            LocalDate toDate = LocalDate.of(2026, 1, 1);

            //when
            Throwable throwable = catchThrowable(() -> LedgerPeriod.of(fromDate, toDate));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(OUT_OF_RANGE)
                    .hasWork("LedgerPeriod 생성")
                    .hasCauseMessage("fromDate > toDate")
                    .hasTarget(LedgerPeriod.class)
                    .hasValue("fromDate", fromDate, "toDate", toDate);
        }

    }

}