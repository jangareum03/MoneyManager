package com.moneymanager.unit.service.ledger;

import com.moneymanager.domain.ledger.dto.request.LedgerWriteRequest;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.security.utils.SecurityUtil;
import com.moneymanager.service.file.FileCommandService;
import com.moneymanager.service.ledger.LedgerCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.service.ledger<br>
 * 파일이름       : LedgerCommandServiceTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 15<br>
 * 설명              : LedgerCommandService 클래스 로직을 검증하는 테스트 클래스
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
 * 		 	  <td>26. 1. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
public class LedgerCommandServiceTest {

	@InjectMocks 	private LedgerCommandService service;

	@Mock	private FileCommandService	fileService;
	@Mock	private SecurityUtil securityUtil;
	@Mock	private LedgerRepository	repository;
	@Mock	private LedgerImageRepository imageRepository;


	//==================[ 📌registerLedger  ]==================
	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("validSuccessWithoutImage")
	@DisplayName("이미지 없는 요청 정보로 가계부가 저장된다.")
	void 가계부_이미지없음_저장성공(String test, LedgerWriteRequest request) throws IOException {
		//given
		when(securityUtil.getMemberId()).thenReturn("member");
		when(repository.insertLedger(any(Ledger.class))).thenReturn(1L);

		//when
		service.registerLedger(request);

		//then
		verify(fileService, never()).storeFile(any());
	}

	static Stream<Arguments> validSuccessWithoutImage(){
		return Stream.of(
				Arguments.of(
						"선택정보X",
						LedgerWriteRequest.builder()
								.date("20251101")
								.category("010101")
								.fixed(false)
								.amount(12000L)
								.paymentType("none")
								.build()
				),
				Arguments.of(
						"선택정보O",
						LedgerWriteRequest.builder()
								.date("20251101")
								.category("020101")
								.fixed(true)
								.period("M")
								.amount(25000L)
								.paymentType("cash")
								.memo("안녕")
								.placeName("장소")
								.roadAddress("기본주소")
								.detailAddress("상세주소")
								.build()
				)
		);
	}

	@ParameterizedTest(name = "[{index}] {0}")
	@MethodSource("validSuccessWithImages")
	@DisplayName("이미지 있는 요청 정보로 가계부가 저장된다.")
	void 이미지_있는_요청정보_저장(String test, LedgerWriteRequest request) throws IOException {
		//given
		when(securityUtil.getMemberId()).thenReturn("member");
		when(repository.insertLedger(any(Ledger.class))).thenReturn(1L);

		Ledger mockLedger = Ledger.builder().id(1L).build();
		when(repository.selectLedgerById(anyLong())).thenReturn(mockLedger);

		//when
		service.registerLedger(request);

		//then
		verify(fileService, atMost(2)).storeFile(any());
		verify(imageRepository, atMost(2)).insertAllImage(anyList());
	}

	static Stream<Arguments> validSuccessWithImages(){
		return Stream.of(
				Arguments.of(
						"선택정보X",
						LedgerWriteRequest.builder()
								.date("20251101")
								.category("010101")
								.fixed(false)
								.amount(12000L)
								.paymentType("none")
								.image(List.of(
										new MockMultipartFile(
												"image",
												"test.png",
												"image/png",
												"abcde".getBytes()
										)
								))
								.build()
				),
				Arguments.of(
						"선택정보O, 이미지O",
						LedgerWriteRequest.builder()
								.date("20251101")
								.category("020101")
								.fixed(true)
								.period("W")
								.amount(39500L)
								.paymentType("bank")
								.memo("안녕")
								.placeName("장소")
								.roadAddress("기본주소")
								.detailAddress("상세주소")
								.image(List.of(
										new MockMultipartFile(
												"image",
												"test1.png",
												"image/png",
												"abcde".getBytes()
										),
										new MockMultipartFile(
												"image",
												"test2.png",
												"image/png",
												"hello, bye".getBytes()
										)
								))
								.build()
				)
		);
	}

	@DisplayName("인증 성공한 회원이 없으면 ClientException이 발생한다.")
	void 인증회원_없음(){}

	@DisplayName("작성한 가계부에서 필수 정보가 없으면 ClientException이 발생한다.")
	void 필수정보_누락(){}

	@DisplayName("작성한 가계부에서 필수 정보가 유효하지 않으면 ClientException이 발생한다.")
	void 필수정보_유효하지_않음() {}

	@DisplayName("작성한 가계부에서 선택 정보가 유효하지 않으면 ClientException이 발생한다.")
	void 선택정보_유효하지_않음(){}

	@DisplayName("작성한 가계부 정보가 비즈니스 규칙과 맞지 않으면 ClientException이 발생한다.")
	void 가계부_작성규칙_위반(){}

	@DisplayName("데이터베이스의 제약조건 위반하면 저장할 수 없다.")
	void 가계부_제약조건_위반(){}

	@DisplayName("이미지가 포함되지 않은 가계부는 saveImage 메서드를 호출하지 않는다.")
	void 이미지_개수별로_이미지저장_호출(){}

	@DisplayName("이미지가 유효하지 않으면 서버에 저장된 파일들이 모두 삭제된다.")
	void 이미지_유효하지_않음(){}
}
