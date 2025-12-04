package com.moneymanager.ledger.search;

import com.moneymanager.dao.main.LedgerDao;
import com.moneymanager.service.main.LedgerService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * <p>
 * 패키지이름    : com.moneymanager.service.main<br>
 * 파일이름       : LedgerServiceSearchTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 25. 11. 20.<br>
 * 설명              : 가계부 검색 기능을 검증하는 테스트 코드를 작성한 클래스
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
 * 		 	  <td>25. 11. 20.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@ExtendWith(MockitoExtension.class)
class LedgerServiceSearchTest {

	@Mock
	private LedgerDao budgetBookDao;

	@InjectMocks
	private LedgerService ledgerService;

}