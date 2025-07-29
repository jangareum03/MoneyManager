package com.areum.moneymanager.dto.inquiry.response;

import com.areum.moneymanager.dto.inquiry.AnswerDTO;
import com.areum.moneymanager.dto.inquiry.QuestionDTO;
import lombok.Builder;
import lombok.Getter;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.dto.inquiry.response<br>
 * * 파일이름       : InquiryDetailResponse<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 25. 7. 26.<br>
 * * 설명              : 문의사항 상세정보 응답을 위한 데이터 클래스
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
 * 		 	  <td>25. 7. 26.</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성 (버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Builder
@Getter
public class InquiryDetailResponse {
	//질문
	private QuestionDetail question;
	//답변
	private AnswerDTO answer;

	/**
	 * 문의사항의 질문 정보를 담은 DTO
	 */
	@Builder
	@Getter
	public static class QuestionDetail {
		//작성 날짜
		private String date;
		//작성자
		private String writer;
		private QuestionDTO questionDTO;
	}
}
