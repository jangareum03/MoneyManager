package com.moneymanager.ledger.repository;

import com.moneymanager.support.data.MemberTestData;
import com.moneymanager.support.fixture.entity.LedgerFixture;
import com.moneymanager.support.fixture.entity.LedgerImageFixture;
import com.moneymanager.support.fixture.entity.MemberFixture;
import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.repository.member.MemberRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.repository<br>
 * 파일이름       : LedgerImageRepositoryIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 7. 16<br>
 * 설명              : LedgerImageRepository 클래스 로직을 검증하는 통합 테스트 클래스
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
 * 		 	  <td>26. 7. 16</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LedgerImageRepositoryIT {

	@Autowired
	private LedgerImageRepository target;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private LedgerRepository ledgerRepository;

	private Ledger ledger;

	@BeforeEach
	void setUp() {
		memberRepository.save(MemberFixture.builder(MemberTestData.MEMBER_ID).build());

		Long id = ledgerRepository.insert(LedgerFixture.newLedger());
		ledger = ledgerRepository.findById(id);
	}


	@Nested
	@DisplayName("이미지 저장")
	class SaveTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("가계부 이미지 정보가 데이터베이스에 저장한다.")
			void savesLedgerImage_whenRequestIsValid() {
				//given: 여러 개의 이미지 정보가 준비되어 있다.
				List<LedgerImage> images = List.of(
						LedgerImageFixture.newImage(ledger.getId(), 1),
						LedgerImageFixture.newImage(ledger.getId(), 2),
						LedgerImageFixture.newImage(ledger.getId(), 3)
				);

				//when: 이미지 정보를 저장한다.
				target.saveAll(images);
				
				//then: 여러 개의 이미지 정보가 저장된다.
				List<LedgerImage> result = target.findByLedgerId(ledger.getId());

				assertThat(result).hasSize(3);
				assertThat(result)
						.extracting(LedgerImage::getLedgerId)
						.allMatch(id -> id.equals(ledger.getId()));

				assertThat(result)
						.extracting(LedgerImage::getCreatedAt)
						.allSatisfy(date -> assertThat(date).isNotNull());

				assertThat(result)
						.extracting(LedgerImage::getUpdatedAt)
						.containsOnlyNulls();
			}
			
			@Test
			@DisplayName("저장된 이미지 경로와 정렬 순서가 올바르게 저장한다.")
			void validatesLedgerImageFields_whenRequestIsValid() {
				//given: 여러 개의 이미지 정보가 준비되어 있다.
				List<LedgerImage> images = List.of(
						LedgerImageFixture.newImage(ledger.getId(), 1),
						LedgerImageFixture.newImage(ledger.getId(), 2),
						LedgerImageFixture.newImage(ledger.getId(), 3)
				);

				//when: 이미지 정보를 저장한다.
				target.saveAll(images);
				
				//then: 저장된 이미지는 오름차순으로 저장된다.
				List<LedgerImage> result = target.findAll();

				assertThat(result)
						.extracting(
								LedgerImage::getImagePath,
								LedgerImage::getSortOrder
						)
						.containsExactly(
								Tuple.tuple("image1.jpg", 1),
								Tuple.tuple("image2.jpg", 2),
								Tuple.tuple("image3.jpg", 3)
						);
			}
			
			@Test
			@DisplayName("빈 이미지 정보면 데이터베이스에 저장되지 않는다.")
			void doesNothing_whenLedgerImageIsEmpty() {
				//given: 빈 리스트가 준비되어 있다.
				List<LedgerImage> images = Collections.emptyList();

				int before = target.count();

				//when: 이미지 정보를 저장한다.
				target.saveAll(images);

				//then: 저장된 이미지 정보가 없다.
				assertThat(target.count()).isEqualTo(before);
			}
			
		}

	}


	@Nested
	@DisplayName("가계부 ID로 조회")
	class SelectByIdTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {
			
			@Test
			@DisplayName("이미지 정보가 저장된 가계부 ID로 조회하면 이미지 정보가 조회된다.")
			void returnsLedgerImages_whenLedgerImagesExist() {
				//given: 여러 개의 이미지 정보가 준비되어 있다.
				List<LedgerImage> images = List.of(
						LedgerImageFixture.newImage(ledger.getId(), 1),
						LedgerImageFixture.newImage(ledger.getId(), 2)
				);

				target.saveAll(images);
				
				//when: 특정 가계부 이미지를 조회한다.
				List<LedgerImage> result = target.findByLedgerId(ledger.getId());
				
				//then: 가계부 ID에 해당하는 이미지 정보가 반환된다.
				assertThat(result)
						.hasSize(2)
						.extracting(
								LedgerImage::getLedgerId
						)
						.containsOnly(ledger.getId());
			}
			
			@Test
			@DisplayName("이미지 정보의 정렬 순서 오름차순으로 조회된다.")
			void sortsLedgerImagesBySequence() {
				//given: 여러 개의 이미지 정보가 준비되어 있다.
				List<LedgerImage> images = List.of(
						LedgerImageFixture.newImage(ledger.getId(), 1),
						LedgerImageFixture.newImage(ledger.getId(), 2),
						LedgerImageFixture.newImage(ledger.getId(), 3)
				);

				target.saveAll(images);

				//when: 특정 가계부 이미지를 조회한다.
				List<LedgerImage> result = target.findByLedgerId(ledger.getId());
				
				//then: 이미지 정렬 순서가 오름차순으로 반환된다.
				assertThat(result)
						.extracting(
								LedgerImage::getSortOrder
						)
						.containsExactly(1, 2, 3);
			}
			
			@Test
			@DisplayName("이미지 정보가 저장되지 않은 가계부 ID로 조회하면 빈 리스트가 반환된다.")
			void returnsEmptyList_whenLedgerImagesAreEmpty() {
				//when: 특정 가계부 이미지를 조회한다.
				List<LedgerImage> result = target.findByLedgerId(ledger.getId());
				
				//then: 빈 리스트가 반환된다.
				assertThat(result).isEmpty();
			}
			
			@Test
			@DisplayName("존재하지 않은 가계부 ID로 조회하면 빈 리스트가 반환된다.")
			void returnsEmptyList_whenLedgerDoesNotExist() {
				//given: 저장되지 않은 가계부 ID가 주어진다.
				Long ledgerId = 99999L;

				//when: 특정 가계부 이미지를 조회한다.
				List<LedgerImage> result = target.findByLedgerId(ledgerId);

				//then: 빈 리스트가 반환된다.
				assertThat(result).isEmpty();
			}
		
		}

	}


	@Nested
	@DisplayName("전체 조회")
	class SelectAllTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("저장된 전체 이미지가 조회한다.")
			void returnsAllImages_whenImagesExist() {
				//given: 여러 개의 이미지 정보가 준비되어 있다.
				List<LedgerImage> images = List.of(
						LedgerImageFixture.newImage(ledger.getId(), 1),
						LedgerImageFixture.newImage(ledger.getId(), 2),
						LedgerImageFixture.newImage(ledger.getId(), 3)
				);

				target.saveAll(images);
				
				//when: 전체 가계부 이미지를 조회한다.
				List<LedgerImage> result = target.findAll();
				
				//then: 모든 가계부 이미지가 반환된다.
				assertThat(result).hasSize(3);
			}
			
			@Test
			@DisplayName("저장된 이미지가 없으면 빈 리스트가 반환된다.")
			void returnsEmptyList_whenImagesDoesNotExist() {
				//when: 전체 가계부 이미지를 조회한다.
				List<LedgerImage> result = target.findAll();

				//then: 모든 가계부 이미지가 반환된다.
				assertThat(result).isEmpty();
			}

		}

	}


	@Nested
	@DisplayName("전체 건수 조회")
	class SelectAllCountTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {
		
			@Test
			@DisplayName("저장된 전체 이미지 건수가 조회된다.")
			void returnsLedgerImageCount_whenLedgerImagesExist() {
				//given: 여러 개의 이미지 정보가 준비되어 있다.
				target.saveAll(
						List.of(
						LedgerImageFixture.newImage(ledger.getId(), 1),
						LedgerImageFixture.newImage(ledger.getId(), 2),
						LedgerImageFixture.newImage(ledger.getId(), 3)
				));
				
				//when: 전체 이미지 개수를 조회한다.
				int result = target.count();
				
				//then: 전체 개수를 반환한다.
				assertThat(result).isEqualTo(3);
			}
			
			@Test
			@DisplayName("저장된 이미지가 없으면 0이 반환된다.")
			void returnsZero_whenLedgerImagesDoNotExist() {
				//when: 전체 이미지 개수를 조회한다.
				int result = target.count();

				//then: 전체 개수를 반환한다.
				assertThat(result).isZero();
			}
			
		}
		
	}


	@Nested
	@DisplayName("전체 삭제")
	class DeleteAllTest {
		
		@Nested
		@DisplayName("성공 케이스")
		class Success {
		
			@Test
			@DisplayName("저장된 이미지 정보가 모두 삭제된다.")
			void deletesAllLedgerImage_whenLedgerImagesExist() {
				//given: 여러 개의 이미지 정보가 저장되어 있다.
				target.saveAll(
						List.of(
								LedgerImageFixture.newImage(ledger.getId(), 1),
								LedgerImageFixture.newImage(ledger.getId(), 2),
								LedgerImageFixture.newImage(ledger.getId(), 3)
						));
				
				//when: 저장된 이미지를 모두 삭제한다.
				target.deleteAll();
				
				//then: 모든 이미지가 삭제된다.
				assertThat(target.count()).isZero();
			}
			
		}
		
	}


	@Nested
	@DisplayName("특정 가계부 삭제")
	class DeleteTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("특정 가계부 ID에 저장된 이미지가 있다면 삭제된 개수가 반환한다.")
			void deletesLedgerImages_whenLedgerImagesExist() {
				//given: 여러 개의 이미지 정보가 저장되어 있다.
				target.saveAll(
						List.of(
								LedgerImageFixture.newImage(ledger.getId(), 1),
								LedgerImageFixture.newImage(ledger.getId(), 2),
								LedgerImageFixture.newImage(ledger.getId(), 3)
						)
				);
				
				//when: 특정 가계부 이미지를 삭제한다.
				int result = target.deleteByLedgerId(ledger.getId());
				
				//then: 삭제된 개수를 반환한다.
				assertThat(result).isEqualTo(3);
			}
			
			@Test
			@DisplayName("특정 가계부 ID에 저장된 이미지가 없다면 0을 반환한다.")
			void deletesLedgerImages_whenLedgerImagesAreEmpty() {
				//when: 특정 가계부 이미지를 삭제한다.
				int result = target.deleteByLedgerId(ledger.getId());

				//then: 0을 반환한다.
				assertThat(result).isZero();
			}
			
			@Test
			@DisplayName("존재하지 않은 가계부 ID로 삭제하면 0을 반환한다.")
			void deletesLedgerImages_whenLedgerDoesNotExist() {
				//given: 저장되지 않은 가계부 ID가 주어진다.
				Long ledgerId = 99999L;

				//when: 특정 가계부 이미지를 삭제한다.
				int result = target.deleteByLedgerId(ledgerId);

				//then: 0을 반환한다.
				assertThat(result).isZero();
			}

		}

	}

}