package com.moneymanager.ledger.service.policy;

import com.moneymanager.ledger.domain.dto.response.ImageSlot;
import com.moneymanager.ledger.domain.enums.SlotStatus;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Named.named;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.service.policy<br>
 * 파일이름       : ImageSlotPolicyTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 13<br>
 * 설명              : ImageSlotPolicy 클래스 로직을 검증하는 단위 테스트 클래스
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
class ImageSlotPolicyTest {

    @InjectMocks
    private ImageSlotPolicy target;

    @Nested
    @DisplayName("등록용 이미지 슬롯을 생성할 때")
    class CreateSlot {

        @Nested
        @DisplayName("성공")
        class Success {

            @ParameterizedTest
            @MethodSource("validCounts")
            @DisplayName("등록 가능 개수가 정책 최대 개수 이하면 등록 가능 개수만큼 빈 슬롯을 생성한다.")
            void createsEmptySlotsByRegisterableCount_whenRegisterableCountIsLessThanOrEqualToPolicyMax(int availImgCnt) {
                //when
                List<ImageSlot> result = target.buildCreatableImageSlots(availImgCnt);

                //then
                assertThat(result.size()).isEqualTo(target.getMAX_SLOT());

                //then: Empty 슬롯이 availImageCnt만큼 생성된다.
                assertThat(result)
                        .filteredOn(ImageSlot::getStatus, SlotStatus.EMPTY)
                        .hasSize(availImgCnt);

                //then: 나머지 슬롯은 LOCK 슬롯이 생성된다.
                assertThat(result)
                        .filteredOn(ImageSlot::getStatus, SlotStatus.LOCKED)
                        .hasSize(target.getMAX_SLOT() - availImgCnt);
            }

            static Stream<Arguments> validCounts() {
                return Stream.of(
                        Arguments.of(
                                named("등록 가능 개수 0 일 때", 0)
                        ),
                        Arguments.of(
                                named("등록 가능 개수 1 일 때", 1)
                        ),
                        Arguments.of(
                                named("등록 가능 개수 2 일 때", 2)
                        ),
                        Arguments.of(
                                named("등록 가능 개수 3 일 때 (경계값)", 3)
                        )
                );
            }

            @Test
            @DisplayName("등록 가능 개수가 정책 최대 개수 초과하면 정책 최대 개수만큼 빈 슬롯을 생성한다.")
            void createsEmptySlotsByPolicyMaxCount_whenRegisterableCountExceedsPolicyMax() {
                //given
                int availImgCnt = target.getMAX_SLOT() + 1;

                //when
                List<ImageSlot> result = target.buildCreatableImageSlots(availImgCnt);

                //then: 모두 Empty 슬롯으로 생성된다.
                assertThat(result.size()).isEqualTo(target.getMAX_SLOT());

                assertThat(result)
                        .allMatch(slot -> slot.getStatus().equals(SlotStatus.EMPTY));


            }

        }

    }


    @Nested
    @DisplayName("상세용 이미지 슬롯을 생성할 때")
    class DetailSlot {

        @Test
        @DisplayName("저장된 이미지 개수만큼 FILLED 슬롯을 생성한다.")
        void createsFilledSlots_whenImagesExist() {
            //given
            List<String> filePath = List.of(
                    "member/2026/01/test1.jpg",
                    "member/2026/01/test2.png"
            );

            //when
            List<ImageSlot> imageSlots = target.buildDisplayImageSlots(filePath);

            //then
            assertThat(imageSlots).hasSize(3);

            List<ImageSlot> filledSlot = imageSlots.stream()
                    .filter(slot -> slot.getStatus() == SlotStatus.FILLED)
                    .toList();

            assertThat(filledSlot.size()).isEqualTo(2);

	        assertThat(filledSlot)
					.extracting(ImageSlot::getFilePath)
					.containsExactly(
							"/uploads/ledger/" + filePath.get(0),
							"/uploads/ledger/" + filePath.get(1)
					);
		 }

        @Test
        @DisplayName("저장된 이미지가 없으면 EMPTY 술롯을 생성한다.")
        void createsEmptySlots_whenImagesDoNotExist() {
            //given
			List<String> filePath = List.of();

            //when
			List<ImageSlot> imageSlots = target.buildDisplayImageSlots(filePath);

            //then
			assertThat(imageSlots).hasSize(3);
			assertThat(imageSlots)
					.extracting(
							ImageSlot::getStatus,
							ImageSlot::getFilePath
					)
					.containsExactly(
							Tuple.tuple(SlotStatus.EMPTY, "/image/ledger/image-empty.svg"),
							Tuple.tuple(SlotStatus.EMPTY, "/image/ledger/image-empty.svg"),
							Tuple.tuple(SlotStatus.EMPTY, "/image/ledger/image-empty.svg")
					);
        }

    }


    @Nested
    @DisplayName("수정용 이미지 슬롯을 생성할 때")
    class EditSlot {
        
        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3})
        @DisplayName("이미지가 없으면 사용 가능한 개수만큼 EMPTY 슬롯을 생성한다.")
        void createsEmptySlots_whenImageDoesNotExist(int availImgCnt) {
        	//given
            List<String> filePath = List.of();

        	//when
            List<ImageSlot> result = target.buildEditableImageSlots(availImgCnt, filePath);
        	
        	//then
        	assertThat(result).hasSize(3);
            assertThat(result)
                    .filteredOn(ImageSlot::getStatus, SlotStatus.EMPTY)
                    .hasSize(availImgCnt);
        }
        
        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("validCounts")
        @DisplayName("이미지가 있으나 사용 가능한 개수가 크면 차이만큼 EMPTY 슬롯을 생성한다.")
        void createsEmptySlotsForDifference_whenAvailableCountIsGreaterThanImageCount(
                String caseName, List<String> filePath, int availImgCnt, int expectedCount
        ) {
            //when
            List<ImageSlot> result = target.buildEditableImageSlots(availImgCnt, filePath);
        	
        	//then
        	assertThat(result).hasSize(3);
            assertThat(result)
                    .filteredOn(ImageSlot::getStatus, SlotStatus.EMPTY)
                    .hasSize(expectedCount);

            assertThat(result)
                    .filteredOn(ImageSlot::getStatus, SlotStatus.FILLED)
                    .hasSize(filePath.size());
        }
        
        static Stream<Arguments> validCounts() {
            return Stream.of(
                    Arguments.of(
                            "이미지 개수: 1, 가능한 개수: 2",
                            List.of("이미지"),
                            2,
                            1
                    ),
                    Arguments.of(
                            "이미지 개수: 1, 가능한 개수: 3",
                            List.of("이미지"),
                            3,
                            2
                    ),
                    Arguments.of(
                            "이미지 개수: 2, 가능한 개수: 3",
                            List.of("이미지", "이미지"),
                            3,
                            1
                    )
            );
        }
        
        @Test
        @DisplayName("저장된 이미지 개수만큼 FILLED 슬롯을 생성한다.")
        void createsFilledSlotsCountOfSavedImages_whenImagesExist() {
        	//given:
            List<String> filePath = List.of("이미지", "이미지", "이미지");

            //when
            List<ImageSlot> result = target.buildEditableImageSlots(3, filePath);

            //then
            assertThat(result).hasSize(3);
            
            assertThat(result)
                    .filteredOn(ImageSlot::getStatus, SlotStatus.FILLED)
                    .hasSize(3);
        }
        
        @Test
        @DisplayName("사용 가능한 개수가 정책상 허용한 개수보다 작으면 LOCKED 슬롯을 생성한다.")
        void createsLockedSlots_whenAvailableCountIsLessThanPolicyLimit() {
            //given:
            List<String> filePath = List.of();

            //when
            List<ImageSlot> result = target.buildEditableImageSlots(2, filePath);

            //then
            assertThat(result).hasSize(3);

            assertThat(result)
                    .filteredOn(ImageSlot::getStatus, SlotStatus.LOCKED)
                    .hasSize(1);
        }

    }

}