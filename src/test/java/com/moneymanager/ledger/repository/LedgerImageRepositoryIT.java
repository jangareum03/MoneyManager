package com.moneymanager.ledger.repository;

import com.moneymanager.ledger.domain.entity.Ledger;
import com.moneymanager.ledger.domain.entity.LedgerImage;
import com.moneymanager.member.domain.entity.Member;
import com.moneymanager.support.IntegrationTest;
import com.moneymanager.support.fixture.entity.LedgerImageTestFixture;
import com.moneymanager.support.fixture.entity.LedgerTestFixture;
import com.moneymanager.support.fixture.entity.MemberTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.ledger.repository<br>
 * 파일이름       : LedgerImageRepositoryIT<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 8. 26<br>
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
 * 		 	  <td>26. 8. 26</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
class LedgerImageRepositoryIT extends IntegrationTest {

    @Autowired
    LedgerImageRepository target;

    @Autowired
    LedgerImageRepository imageRepository;

    Member member;

    @BeforeEach
    void setUp() {
        member = MemberTestFixture.builder().build(passwordEncoder);

        insertMember(member);
    }

    @Nested
    @DisplayName("가계부 코드로 이미지 조회할 때")
    class FindByCode {
        
        @Test
        @DisplayName("존재하는 코드면 코드에 해당하는 이미지 정보를 조회한다.")
        void findsImages_whenCodeExists() {
        	//given: 가계부와 해당 이미지가 저장되어 있다.
            Long id = ledgerRepository.save(
                    LedgerTestFixture.builder().build()
            );

            Ledger ledger = ledgerRepository.findById(id);

            imageRepository.saveAll(
                    List.of(
                            LedgerImageTestFixture.builder(id, Path.of("root")).build()
                    )
            );
        	
        	//when
            List<LedgerImage> result = target.findByLedgerCode(ledger.getCode());
        	
        	//then
            assertThat(result.size()).isEqualTo(1);
        }
        
        @Test
        @DisplayName("존재하지 않은 코드면 빈 리스트로 조회한다.")
        void findsEmptyList_whenCodeDoesNotExist() {
        	//given
            String code = "no-exist";
        	
        	//when
            List<LedgerImage> result = target.findByLedgerCode(code);
        	
        	//then
        	assertThat(result).isEmpty();
        }

    }

}