package com.moneymanager.unit.repository.ledger;

import com.moneymanager.config.DatabaseConfig;
import com.moneymanager.repository.ledger.CategoryRepository;
import com.moneymanager.domain.ledger.entity.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * <p>
 * 패키지이름    : com.moneymanager.unit.repository.ledger<br>
 * 파일이름       : CategoryRepositoryTest<br>
 * 작성자          : areum Jang<br>
 * 생성날짜       : 26. 1. 7<br>
 * 설명              : 가계부 카테고리와 관련된 테이블의 데이터를 조작하는 기능을 검증하는 클래스
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
 * 		 	  <td>26. 1. 7.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@JdbcTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
		DatabaseConfig.class,
		CategoryRepository.class
})
public class CategoryRepositoryTest {

	@Autowired	private CategoryRepository repository;

	//==================[ 📌findAllCategory  ]==================
	@Test
	@DisplayName("가계부 카테고리에서 모든 레벨의 카테고리를 조회한다.")
	void 모든_카테고리_조회(){
		//when
		List<Category> result = repository.findAllCategory();

		//then
		assertThat(result).isNotEmpty()
						.extracting(Category::getCode)
								.doesNotHaveDuplicates();

		assertThat(result)
				.allSatisfy(c -> {
					assertThat(c.getCode()).hasSize(6);
					assertThat(c.getName()).isNotNull();

					if( c.getParentCode() != null ) {
						assertThat(
								result.stream()
										.map(Category::getCode)
										.collect(Collectors.toList())
						).contains(c.getParentCode());
					}
				});

		assertThat(result)
				.anyMatch(c -> c.getParentCode() == null);
	}
}
