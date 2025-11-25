package com.moneymanager.ledger.info;

import com.moneymanager.dao.main.LedgerDao;
import com.moneymanager.domain.ledger.entity.Ledger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.budgetBook.info<br>
 * 파일이름       : LedgerDaoInfoTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 24.<br>
 * 설명              : 가계부 정보를 DB에서 조회 가능한지 확인하는 테스트 클래스
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
 * 		 	  <td>25. 11. 24.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@SpringBootTest
@ActiveProfiles("test")
public class LedgerDaoInfoTest {

	@Autowired
	private LedgerDao budgetBookDao;

	@BeforeEach
	void 초기화() {

	}

	//=================================================
	// findBudgetBookById() 테스트
	//=================================================
	@Test
	void 가게부번호_있으면_한건반환(){
		//given
		long id = 1;

		//when
		Ledger result = budgetBookDao.findLedgerById(id);

		//then
		assertThat(result).isNull();
	}
}
