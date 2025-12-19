package com.moneymanager.ledger.info;

import com.moneymanager.dao.main.LedgerDao;
import com.moneymanager.domain.global.dto.ErrorDTO;
import com.moneymanager.domain.ledger.dto.*;
import com.moneymanager.domain.ledger.entity.Category;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.domain.ledger.enums.FixedPeriod;
import com.moneymanager.domain.ledger.enums.FixedYN;
import com.moneymanager.domain.ledger.enums.LedgerType;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

	@DisplayName("가계부가 일회성인 정보면 고정주기가 null이여야 한다.")
	@Test
	void 일회성인_가계부정보면_고정주기_null반환(){
		//given
		String memberId = "member";
		String id = "1";

		Ledger ledger = createLedgerByFixN(memberId);
		when(ledgerDao.findLedgerEditForUser(id)).thenReturn(ledger);
		when(imageService.getLimitImageCount(memberId)).thenReturn(1);

		List<CategoryResponse> categoryResponses = createIncomeCategory();
		when(categoryService.getAncestorCategoriesByCode(ledger.getCategory().getCode()))
				.thenReturn(categoryResponses);

		List<LedgerImage> images = createMinLedgerImage(ledger.getId());
		when(imageService.getImageListByLedger(ledger.getNum(), 1)).thenReturn(images);

		//when
		LedgerEditResponse result = service.getLedgerEdit(memberId, id);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getDate()).isEqualTo("2025. 11. 01 (토)");
		assertThat(result.getMemo()).isEqualTo("메모");
		assertThat(result.getPlace()).isNull();

		assertThat(result.getType()).isSameAs(LedgerType.INCOME);
		assertThat(result.getCategory()).isSameAs(categoryResponses);

		assertThat(result.getImages())
				.containsExactly("/2025/11/01/이미지1.png", null, null);

		assertThat(result.getAmountInfo().getAmount()).isEqualTo(10000L);
		assertThat(result.getAmountInfo().getType()).isSameAs(PaymentType.NONE);

		assertThat(result.getFixed().isFix()).isFalse();
		assertThat(result.getFixed().getPeriod()).isNull();
	}

	@DisplayName("가계부가 반복적인 정보면 고정주기 값이 있어야 한다.")
	@Test
	void 반복인_가계부정보면_고정주기_l반환(){
		//given
		String memberId = "member";
		String id = "1";

		Ledger ledger = createLedgerByFixY(memberId);
		when(ledgerDao.findLedgerEditForUser(id)).thenReturn(ledger);
		when(imageService.getLimitImageCount(memberId)).thenReturn(1);

		List<CategoryResponse> categoryResponses = createIncomeCategory();
		when(categoryService.getAncestorCategoriesByCode(ledger.getCategory().getCode()))
				.thenReturn(categoryResponses);

		List<LedgerImage> images = createMinLedgerImage(ledger.getId());
		when(imageService.getImageListByLedger(ledger.getNum(), 1)).thenReturn(images);

		//when
		LedgerEditResponse result = service.getLedgerEdit(memberId, id);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getDate()).isEqualTo("2025. 11. 01 (토)");
		assertThat(result.getMemo()).isEqualTo("메모");
		assertThat(result.getPlace()).isNull();

		assertThat(result.getType()).isSameAs(LedgerType.INCOME);
		assertThat(result.getCategory()).isSameAs(categoryResponses);

		assertThat(result.getImages())
				.containsExactly("/2025/11/01/이미지1.png", null, null);

		assertThat(result.getAmountInfo().getAmount()).isEqualTo(10000L);
		assertThat(result.getAmountInfo().getType()).isSameAs(PaymentType.NONE);

		assertThat(result.getFixed().isFix()).isTrue();
		assertThat(result.getFixed().getPeriod()).isEqualTo(FixedPeriod.WEEKLY.getValue());
	}

	private Ledger createLedgerByFixN(String memberId) {
		return Ledger.builder()
				.id("ledger1")
				.member(Member.builder().id(memberId).build())
				.fixed(FixedYN.VARIABLE)
				.category(Category.builder().code("010101").name("월급").build())
				.date(new LedgerDate("20251101"))
				.memo("메모")
				.amountInfo(AmountInfo.builder().amount(10000L).type(PaymentType.NONE).build())
				.build();
	}

	private Ledger createLedgerByFixY(String memberId) {
		return Ledger.builder()
				.id("ledger1")
				.member(Member.builder().id(memberId).build())
				.fixed(FixedYN.REPEAT)
				.cycleType(FixedPeriod.WEEKLY)
				.category(Category.builder().code("010101").name("월급").build())
				.date(new LedgerDate("20251101"))
				.memo("메모")
				.amountInfo(AmountInfo.builder().amount(10000L).type(PaymentType.NONE).build())
				.build();
	}

	private List<CategoryResponse> createIncomeCategory(){
		return List.of(
				CategoryResponse.builder().code("010000").name("수입").build(),
				CategoryResponse.builder().code("010100").name("소득").build(),
				CategoryResponse.builder().code("010101").name("월급").build()
		);
	}

	private List<LedgerImage> createMinLedgerImage(String ledgerId){
		List<LedgerImage> images = new ArrayList<>();

		images.add(LedgerImage.builder().
				ledgerId(Ledger.builder().id(ledgerId).build())
				.imagePath("/2025/11/01/이미지1.png")
				.build());
		images.add(null);
		images.add(null);

		return images;
	}



	//=================================================
	// getWriteByData() 테스트
	//=================================================
	@DisplayName("요청값이 모두 정상이라면 LedgerWriteStep2Response 객체를 반환한다.")
	@Test
	void 정상값이면_응답DTO_반환(){
		//given
		String id = "member";
		String type = "income";
		String date = "20251201";

		LedgerType ledgerType = LedgerType.INCOME;

		List<CategoryResponse> categoryResponse = List.of(
			CategoryResponse.builder().code("010100").name("소득").build(),
			CategoryResponse.builder().code("010200").name("저축").build()
		);
		when(categoryService.getSubCategories(any())).thenReturn(categoryResponse);

		List<Boolean> imageSlots = List.of(true, false, false);
		when(imageService.getImageSlots(id)).thenReturn(imageSlots);

		//when
		LedgerWriteStep2Response result = service.getWriteByData(id, type ,date);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getTitle()).isEqualTo("2025년 12월 01일 월요일");
		assertThat(result.getType()).isEqualTo(ledgerType);
		assertThat(result.getFixed()).containsExactly(FixedYN.values());
		assertThat(result.getCategories()).isEqualTo(categoryResponse);
		assertThat(result.getPaymentTypes()).containsExactly(PaymentType.values());
		assertThat(result.getImageSlot()).isEqualTo(imageSlots);
	}
}