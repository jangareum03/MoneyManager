package com.moneymanager.ledger.service.read;

import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.repository.LedgerRepository;
import com.moneymanager.support.ApplicationExceptionAssert;
import com.moneymanager.support.fixture.entity.LedgerTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static com.moneymanager.global.exception.code.ErrorCode.DATA_NOT_FOUND;
import static com.moneymanager.global.exception.code.ErrorCode.OWNER_ONLY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.read<br>
 * 파일이름       : LedgerReadServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 23<br>
 * 설명              : LedgerReadService 클래스 로직을 검증하는 단위 테스트 클래스
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
 * 		 	  <td>26. 8. 23</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class LedgerReadServiceTest {

    @InjectMocks
    private LedgerReadService target;

    @Mock
    LedgerRepository ledgerRepository;

    @Nested
    @DisplayName("자신의 가계부 여러개를 조회할 때")
    class GetOwnerLedgers {

        @Test
        @DisplayName("코드 리스트가 비어있으면 빈 리스트를 반환한다.")
        void returnsEmptyList_whenLedgerCodesAreEmpty() {
            //given
            String memberId = "memberId";
            List<String> codes = List.of();

            //when
            List<Ledger> result = target.getOwnerLedgers(memberId, codes);

            //then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("조회한 가계부가 null이면 리스트에 추가하지 않는다.")
        void doesNotAddToList_whenLedgerIsNull() {
            //given
            String memberId = "memberId";
            List<String> codes = List.of("code1", "code2", "code3");

            when(ledgerRepository.findByCodeIn(codes))
                    .thenReturn(
                            Arrays.asList(
                                    LedgerTestFixture.builder().code("code1").memberId(memberId).build(),
                                    LedgerTestFixture.builder().code("code3").memberId(memberId).build()
                            )
                    );

            //when
            List<Ledger> result = target.getOwnerLedgers(memberId, codes);

            //then
            assertThat(result)
                    .extracting(Ledger::getCode)
                    .doesNotContain("code2");
        }

        @Test
        @DisplayName("타인의 가계부는 리스트에 추가하지 않는다.")
        void doesNotAddToList_whenLedgerBelongsToOtherUser() {
            //given
            String memberId = "memberId";
            List<String> codes = List.of("code1", "code2", "code3");

            when(ledgerRepository.findByCodeIn(codes))
                    .thenReturn(
                            Arrays.asList(
                                    LedgerTestFixture.builder().code("code1").memberId(memberId).build(),
                                    LedgerTestFixture.builder().code("code2").memberId("other").build(),
                                    LedgerTestFixture.builder().code("code3").memberId(memberId).build()
                            )
                    );

            //when
            List<Ledger> result = target.getOwnerLedgers(memberId, codes);

            //then
            assertThat(result)
                    .extracting(Ledger::getCode)
                    .doesNotContain("code2");
        }

    }


    @Nested
    @DisplayName("자신의 가계부 하나를 조회할 때")
    class GetOwnerLedger {

        @Test
        @DisplayName("본인의 가계부 코드면 가계부를 조회한다.")
        void returnsLedger_whenLedgerCodeBelongsToUser() {
            //given
            String code = "code";
            String memberId = "memberId";

            when(ledgerRepository.findByCode(code))
                    .thenReturn(LedgerTestFixture.builder().memberId(memberId).code(code).build());

            //when
            Ledger result = target.getOwnerLedger(memberId, code);

            //then
            assertThat(result.getMemberId()).isEqualTo(memberId);
            assertThat(result.getCode()).isEqualTo(code);
        }

        @Test
        @DisplayName("존재하지 않은 코드면 예외를 발생시킨다")
        void throwsException_whenLedgerDoesNotExist() {
            //given
            String code = "code";
            String memberId = "memberId";

            when(ledgerRepository.findByCode(code))
                    .thenReturn(null);

            //when
            Throwable throwable = catchThrowable(() -> target.getOwnerLedger(memberId, code));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(DATA_NOT_FOUND)
                    .hasWork("가계부 조회")
                    .hasTarget(Ledger.class)
                    .hasValue("code", code);
        }

        @Test
        @DisplayName("타인의 가계부 코드면 예외를 발생시킨다.")
        void throwsException_whenLedgerCodeBelongsToAnotherUser() {
            //given
            String code = "code";
            String memberId = "memberId";

            when(ledgerRepository.findByCode(code))
                    .thenReturn(LedgerTestFixture.builder().memberId("other").build());

            //when
            Throwable throwable = catchThrowable(() -> target.getOwnerLedger(memberId, code));

            //then
            ApplicationExceptionAssert.assertThatApplicationException(throwable)
                    .hasErrorCode(OWNER_ONLY)
                    .hasWork("가계부 작성자 확인")
                    .hasTarget(Ledger.class)
                    .hasValue("code", code, "requester", memberId);
        }

    }

}