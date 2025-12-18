package com.moneymanager.ledger.info;

import com.moneymanager.dao.main.LedgerDao;
import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.domain.ledger.dto.CategoryResponse;
import com.moneymanager.domain.ledger.dto.LedgerDetailResponse;
import com.moneymanager.domain.ledger.dto.LedgerEditResponse;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.domain.ledger.enums.PaymentType;
import com.moneymanager.domain.ledger.vo.AmountInfo;
import com.moneymanager.domain.ledger.vo.LedgerDate;
import com.moneymanager.domain.member.Member;
import com.moneymanager.exception.ErrorCode;
import com.moneymanager.exception.custom.ClientException;
import com.moneymanager.service.main.CategoryService;
import com.moneymanager.service.main.ImageServiceImpl;
import com.moneymanager.service.main.LedgerService;
import com.moneymanager.service.main.validation.DateScopeValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.info<br>
 * 파일이름       : LedgerServiceInfoTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 12. 8<br>
 * 설명              : 가계부 내역 정보를 조회 기능을 확인하는 테스트 클래스
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
 * 		 	  <td>25. 12. 8.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
public class LedgerServiceInfoTest {

	@InjectMocks
	private LedgerService service;

	@Mock	private ImageServiceImpl imageService;
	@Mock	private CategoryService	categoryService;
	@Mock	private DateScopeValidator validator;
	@Mock	private LedgerDao	ledgerDao;


	//=================================================
	// getLedgerDetail() 테스트
	//=================================================
	@DisplayName("없는 가계부 번호로 DB에서 조회 시 ClientException 예외가 발생한다.")
	@Test
	void 가계부_없으면_예외발생(){
		//given
		String memberId = "member";
		String id = "1";

		when(ledgerDao.findLedgerDetailForUser(id)).thenThrow(EmptyResultDataAccessException.class);

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(()-> service.getLedgerDetail(memberId, id))
				.satisfies( e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_ID_NONE);
					assertThat(errorDTO.getMessage()).isEqualTo("존재하지 않는 가계부입니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(id);
				});

	}

	@DisplayName("작성하지 않은 가계부는 접근이 불가능하고 ClientException 예외가 발생한다.")
	@Test
	void 작성자_다르면_예외발생(){
		//given
		String memberId = "member";
		String id = "1";

		Ledger ledger = Ledger.builder()
				.member(Member.builder().id("not").build())
				.category(Category.builder().code("010101").name("월급").build())
				.date(new LedgerDate("20151101"))
				.memo("메모")
				.amountInfo(AmountInfo.builder().amount(10000L).type(PaymentType.NONE).build())
				.build();
		when(ledgerDao.findLedgerDetailForUser(id)).thenReturn(ledger);

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> service.getLedgerDetail(memberId, id))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.MEMBER_ID_MISMATCH);
					assertThat(errorDTO.getMessage()).isEqualTo("작성자가 아닌 사용자는 해당 가계부에 접근이 불가능합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(memberId);
				});
	}

	@DisplayName("가계부 번호와 회원이 일치하면 DB에서 상세정보를 조회한다.")
	@Test
	void 인자값_정상이면_조회완료(){
		//given
		String memberId = "member";
		String id = "1";

		Ledger ledger = Ledger.builder()
				.num(1L)
				.member(Member.builder().id(memberId).build())
				.category(Category.builder().code("010101").name("월급").build())
				.date(new LedgerDate("20251101"))
				.memo("메모")
				.amountInfo(AmountInfo.builder().amount(10000L).type(PaymentType.NONE).build())
				.build();

		List<LedgerImage> mockImages = List.of(
				LedgerImage.builder()
						.id(1L)
						.ledgerId(Ledger.builder().id("ledger1").build())
						.imagePath("/2025/11/23/b8d3c9a1-4f8e-4a7b-8c6d-5e9f2a1b0c4d.png")
						.sortOrder(2)
						.createdAt(LocalDateTime.of(2025, 11, 23, 11,11,11))
						.build()
		);

		when(ledgerDao.findLedgerDetailForUser(id)).thenReturn(ledger);
		when(imageService.getLimitImageCount(memberId)).thenReturn(1);
		when(imageService.getImageListByLedger(ledger.getNum(), 1)).thenReturn(mockImages);

		//when
		LedgerDetailResponse result = service.getLedgerDetail(memberId, id);

		//then
		assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(
						LedgerDetailResponse.builder()
								.date("2025. 11. 01 (토)")
								.category(CategoryResponse.from(ledger.getCategory()))
								.memo("메모")
								.amountInfo(ledger.getAmountInfo())
								.place(ledger.getPlace())
								.images(
										List.of(
												"/2025/11/23/b8d3c9a1-4f8e-4a7b-8c6d-5e9f2a1b0c4d.png"
										)
								)
				);
	}



	//=================================================
	// getLedgerEdit() 테스트
	//=================================================
	@DisplayName("없는 가계부 번호로 DB에서 조회 시 ClientException 예외가 발생한다.")
	@Test
	void 가계부_없으면_수정화면_접근불가(){
		//given
		String memberId = "member";
		String id = "1";

		when(ledgerDao.findLedgerEditForUser(id)).thenThrow(EmptyResultDataAccessException.class);

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(()-> service.getLedgerEdit(memberId, id))
				.satisfies( e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.LEDGER_ID_NONE);
					assertThat(errorDTO.getMessage()).isEqualTo("존재하지 않는 가계부입니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(id);
				});

	}

	@DisplayName("작성하지 않은 가계부는 접근이 불가능하고 ClientException 예외가 발생한다.")
	@Test
	void 작성자_다르면_수정화면_접근불가(){
		//given
		String memberId = "member";
		String id = "1";

		Ledger ledger = Ledger.builder()
				.member(Member.builder().id("not").build())
				.category(Category.builder().code("010101").name("월급").build())
				.date(new LedgerDate("20151101"))
				.memo("메모")
				.amountInfo(AmountInfo.builder().amount(10000L).type(PaymentType.NONE).build())
				.build();
		when(ledgerDao.findLedgerEditForUser(id)).thenReturn(ledger);

		//when & then
		assertThatExceptionOfType(ClientException.class)
				.isThrownBy(() -> service.getLedgerEdit(memberId, id))
				.satisfies(e -> {
					ErrorDTO<?> errorDTO = e.getErrorDTO();

					assertThat(errorDTO.getErrorCode()).isSameAs(ErrorCode.MEMBER_ID_MISMATCH);
					assertThat(errorDTO.getMessage()).isEqualTo("작성자가 아닌 사용자는 해당 가계부에 접근이 불가능합니다.");
					assertThat(errorDTO.getRequestData()).isEqualTo(memberId);
				});
	}

	@DisplayName("가계부 번호와 회원이 일치하면 DB에서 수정화면에 필요한 정보를 조회한다.")
	@Test
	void 인자값_정상이면_수정정보_반환(){
		//given
		String memberId = "member";
		String id = "1";

		Ledger ledger = Ledger.builder()
				.member(Member.builder().id(memberId).build())
				.category(Category.builder().code("010101").name("월급").build())
				.date(new LedgerDate("20151101"))
				.memo("메모")
				.amountInfo(AmountInfo.builder().amount(10000L).type(PaymentType.NONE).build())
				.build();
		when(ledgerDao.findLedgerEditForUser(id)).thenReturn(ledger);
		when(imageService.getLimitImageCount(memberId)).thenReturn(1);

		List<CategoryResponse> categoryResponses = List.of(
				CategoryResponse.builder().code("010000").name("수입").build(),
				CategoryResponse.builder().code("010100").name("소득").build(),
				CategoryResponse.builder().code("010101").name("월급").build()
		);
		when(categoryService.getAncestorCategoriesByCode(ledger.getCategory().getCode()))
				.thenReturn(categoryResponses);

		//when
		LedgerEditResponse result = service.getLedgerEdit(memberId, id);

		//then
		assertThat(result)
				.usingRecursiveComparison()
				.isEqualTo(LedgerEditResponse.from(ledger, categoryResponses));
	}
}
