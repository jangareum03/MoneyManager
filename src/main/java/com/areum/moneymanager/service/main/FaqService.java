package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dto.response.main.FaqResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;



/**
 * <p>
 *  * 패키지이름    : com.areum.moneymanager.service.main<br>
 *  * 파일이름       : FaqService<br>
 *  * 작성자          : areum Jang<br>
 *  * 생성날짜       : 25. 7. 15<br>
 *  * 설명              : 자주묻는질문 관련 비즈니스 로직을 처리하는 클래스
 * </p>
 * <br>
 * <p color='#FFC658'>📢 변경이력</p>
 * <table border="1" cellpadding="5" cellspacing="0" style="width: 100%">
 *		<thead>
 *		 	<tr style="border-top: 2px solid; border-bottom: 2px solid">
 *		 	  	<td>날짜</td>
 *		 	  	<td>작성자</td>
 *		 	  	<td>변경내용</td>
 *		 	</tr>
 *		</thead>
 *		<tbody>
 *		 	<tr style="border-bottom: 1px dotted">
 *		 	  <td>25. 7. 15</td>
 *		 	  <td>areum Jang</td>
 *		 	  <td>클래스 전체 리팩토링(버전 2.0)</td>
 *		 	</tr>
 *		</tbody>
 * </table>
 */
@Service
public class FaqService {

	private final Logger logger = LogManager.getLogger(this);


	/**
	 * 특정 json파일의 내용을 리스트로 담아 반환합니다.
	 *
	 * @return	FAQ의 질문과 답변 리스트
	 * @throws IOException
	 */
	public List<FaqResponseDTO.Read> getFqaList() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		InputStream is = getClass().getResourceAsStream("/static/data/faq.json");

		return Arrays.asList( mapper.readValue( is, FaqResponseDTO.Read[].class) );
	}
}
