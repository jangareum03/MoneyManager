package com.moneymanager.service.file;


import com.moneymanager.domain.ledger.entity.Ledger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;


/**
 * <p>
 * 패키지이름    : com.moneymanager.service.file<br>
 * 파일이름       : LedgerImageStorageStrategy<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 3. 18<br>
 * 설명              : 가계부 파일 이름을 지정하는 클래스
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
 * 		 	  <td>26. 3. 18.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Component
public class LedgerImageStorageStrategy extends ImageStorageStrategy<Ledger> {

	public LedgerImageStorageStrategy(Clock clock, @Value("${file.image.ledger.storage-path}") String rootPath) {
		super(clock, rootPath);
	}

	@Override
	protected String generateRelativePath(Ledger ledger) {
		LocalDate now = LocalDate.now(clock);

		String memberId = ledger.getMemberId();
		String year = String.valueOf(now.getYear());
		String month = String.format("%02d", now.getMonthValue());

		return String.join("/", memberId, year, month);
	}

	@Override
	public Path generateAbsolutePath(String relativePath) {
		return Path.of(getRootPath(), relativePath);
	}

}