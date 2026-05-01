package com.moneymanager.integration.repository.ledger;

import com.moneymanager.domain.ledger.entity.Ledger;
import com.moneymanager.domain.ledger.entity.LedgerImage;
import com.moneymanager.domain.member.Member;
import com.moneymanager.repository.ledger.LedgerImageRepository;
import com.moneymanager.repository.ledger.LedgerRepository;
import com.moneymanager.repository.member.MemberRepository;
import com.moneymanager.fixture.MemberFixture;
import com.moneymanager.fixture.ledger.LedgerFixture;
import com.moneymanager.fixture.ledger.LedgerImageFixture;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.repository.ledger<br>
 * 파일이름       : LedgerImageRepositoryTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 13<br>
 * 설명              : 가계부 이미지와 관련된 테이블의 데이터 조작 기능을 검증하는 클래스
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
 * 		 	  <td>26. 1. 13.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LedgerImageRepositoryTest {

	@Autowired
	private LedgerImageRepository target;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private LedgerRepository ledgerRepository;

	private Member member;
	private Ledger ledger;
	private List<LedgerImage> images;

	@BeforeEach
	void setUp() {
		//회원
		member = memberRepository.save(MemberFixture.defaultMember().build());

		//가계부
		Long id = ledgerRepository.save(LedgerFixture.defaultLedger().id(null).memberId(member.getId()).build());
		ledger = ledgerRepository.findById(id);

		//이미지
		images = List.of(
				LedgerImageFixture.defaultImage(1L, ledger.getId()).sortOrder(1).build(),
				LedgerImageFixture.defaultImage(2L,ledger.getId()).sortOrder(2).build(),
				LedgerImageFixture.defaultImage(3L, ledger.getId()).sortOrder(3).build()
		);
	}


	@Nested
	@DisplayName("가계부이미지 저장")
	class SaveTest {

		@Nested
		@DisplayName("성공 케이스")
		class Success {

			@Test
			@DisplayName("여러 건의 이미지 정보는 데이터베이스에 반영된다.")
			void savesLedgerImages_whenLedgerImageListIsValid(){
				//when
				target.saveAll(images);

				//then
				Integer result = target.count();

				assertThat(result).isEqualTo(3);
			}

			@Test
			@DisplayName("빈 이미지 리스트는 데이터베이스에 반영되지 않는다.")
			void doesNothing_whenLedgerImageListIsEmpty() {
				//given
				images = Collections.emptyList();

				//when
				target.saveAll(images);

				//when
				Integer result = target.count();

				//then
				assertThat(result).isZero();
			}
		}

	}


	@Nested
	@DisplayName("가계부이미지 조회")
	class FindTest {

		@Nested
		@DisplayName("특정가계부 이미지 조회")
		class FindByLedgerId {

			@Test
			@DisplayName("가계부ID에 해당하는 이미지 리스트를 조회한다.")
			void returnsLedgerImages_whenLedgerIdIsExists() {
				//given
				target.saveAll(images);
				Long id = ledger.getId();

				//when
				List<LedgerImage> result = target.findByLedgerId(id);

				//then
				assertThat(result)
						.hasSize(3)
						.allMatch(i -> i.getLedgerId().equals(id));

				assertThat(result)
						.extracting(LedgerImage::getId)
						.doesNotHaveDuplicates();

				assertThat(result)
						.extracting(LedgerImage::getSortOrder)
						.containsExactly(1, 2, 3);
			}

			@Test
			@DisplayName("가계부ID의 등록된 이미지가 없으면 빈 리스트를 반환한다.")
			void returnsEmptyList_whenNoImagesInLedger() {
				//given
				Long id = ledger.getId();

				//when
				List<LedgerImage> result = target.findByLedgerId(id);

				//then
				assertThat(result).isEmpty();
			}

			@Test
			@DisplayName("없는 가계부 ID로 조회하면 빈 리스트를 반환한다.")
			void returnsEmptyList_whenLedgerIdDoesNotExist() {
				//given
				Long id = 100L;

				//when
				List<LedgerImage> result = target.findByLedgerId(id);

				//then
				assertThat(result).isEmpty();
			}

			@Test
			@DisplayName("가계부ID가 null이면 빈 리스트를 반환한다.")
			void returnsEmptyList_whenLedgerIdIsNull() {
				//given
				Long id = null;

				//when
				List<LedgerImage> result = target.findByLedgerId(id);

				//then
				assertThat(result).isEmpty();
			}

		}

		@Nested
		@DisplayName("전체 조회")
		class FindAllTest {

			@Test
			@DisplayName("저장된 가계부이미지가 모두 조회횐다.")
			void returnsAllLedgerImages_whenLedgerImagesExist() {
				//when
				List<LedgerImage> result = target.findAll();

				//then
				assertThat(result).hasSize(3);
			}

			@Test
			@Sql(statements = "DELETE FROM ledger_image", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("저장된 가계부이미지가 없으면 빈 리스트가 반환된다.")
			void returnsEmptyList_whenNoLedgerImages() {
				//when
				List<LedgerImage> result = target.findAll();

				//then
				assertThat(result).hasSize(0);
			}

		}


		@Nested
		@DisplayName("총개수 조회")
		class FindCount {

			@Test
			@DisplayName("저장된 가계부이미지 총 개수를 조회한다.")
			void returnsLedgerImageCount_whenLedgerImagesExist() {
				//when
				Integer result = target.count();

				//then
				assertThat(result).isEqualTo(3);
			}

			@Test
			@Sql(statements = "DELETE FROM ledger_image", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
			@DisplayName("저장된 가계부이미지가 없으면 개수는 0이다.")
			void returnsZero_whenNoLedgerImagesExist() {
				//when
				Integer result = target.count();

				//then
				assertThat(result).isZero();
			}

		}

	}


	@Nested
	@DisplayName("가계부이미지 삭제")
	class DeleteTest {

		@Test
		@DisplayName("저장된 가계부이미지를 모두 삭제한다.")
		void returnsZero_whenAllLedgerImagesAreDeleted() {
			//given
			LedgerImage d1 = LedgerImageFixture.defaultImage(1L, ledger.getId()).sortOrder(1).build();
			LedgerImage d2 = LedgerImageFixture.defaultImage(2L, ledger.getId()).sortOrder(2).build();
			LedgerImage d3 = LedgerImageFixture.defaultImage(3L, ledger.getId()).sortOrder(3).build();

			List<LedgerImage> images = List.of(d1, d2, d3);

			target.saveAll(images);

			//when
			target.deleteAll();
			Integer result = target.count();

			//then
			assertThat(result).isZero();
		}

	}

}
