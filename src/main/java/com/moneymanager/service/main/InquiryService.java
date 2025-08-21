package com.moneymanager.service.main;

import com.moneymanager.dao.main.QnADao;
import com.moneymanager.dto.common.request.PageRequest;
import com.moneymanager.dto.common.response.PageResponse;
import com.moneymanager.dto.inquiry.AnswerDTO;
import com.moneymanager.dto.inquiry.QuestionDTO;
import com.moneymanager.dto.inquiry.request.InquiryAccessRequest;
import com.moneymanager.dto.inquiry.request.InquirySearchRequest;
import com.moneymanager.dto.inquiry.request.InquiryUpdateRequest;
import com.moneymanager.dto.inquiry.response.InquiryDetailResponse;
import com.moneymanager.dto.inquiry.response.InquiryListResponse;
import com.moneymanager.dto.inquiry.response.InquirySearchResponse;
import com.moneymanager.dto.inquiry.response.InquiryUpdateResponse;
import com.moneymanager.entity.Answer;
import com.moneymanager.entity.Member;
import com.moneymanager.entity.Question;
import com.moneymanager.enums.type.AnswerStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * * 패키지이름    : com.areum.moneymanager.service.main<br>
 * * 파일이름       : InquiryService<br>
 * * 작성자          : areum Jang<br>
 * * 생성날짜       : 23. 2. 11<br>
 * * 설명              : 문의사항 관련 비즈니스 로직을 처리하는 클래스
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
 * 		 	  <td>23. 2. 11</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>최초 생성(버전 1.0)</td>
 * 		 	</tr>
 * 		 	<tr style="border-bottom: 1px dotted">
 * 		 	  <td>25. 7. 15</td>
 * 		 	  <td>areum Jang</td>
 * 		 	  <td>[리팩토링] 코드 정리(버전 2.0)</td>
 * 		 	</tr>
 * 		</tbody>
 * </table>
 */
@Service
public class InquiryService {

	private final Logger logger = LogManager.getLogger(this);
	private final QnADao qnADao;

	public InquiryService(QnADao qnADao) {
		this.qnADao = qnADao;
	}


	@Transactional
	public Long createQuestion(String memberId, QuestionDTO question) {
		char open = question.isOpen() ? 'Y' : 'N';

		Question entity = Question.builder()
				.member(Member.builder().id(memberId).build())
				.title(question.getTitle()).content(question.getContent()).open(open)
				.build();

		return qnADao.saveQuestion(entity);
	}


	public Long updateQuestion(String memberId, InquiryUpdateRequest update) {
		QuestionDTO question = update.getQuestion();

		char open = question.isOpen() ? 'Y' : 'N';

		Question entity = Question.builder()
				.member(Member.builder().id(memberId).build())
				.id(update.getId())
				.title(question.getTitle()).content(question.getContent()).open(open)
				.build();

		if (qnADao.updateQuestion(memberId, entity)) {
			return entity.getId();
		}

		return 0L;
	}


	public InquiryListResponse getInquiriesBySearch(InquirySearchRequest search) {
		InquirySearchRequest initSearch = initSearch(search);

		int totalCount = qnADao.countQuestionBySearch(initSearch);
		PageResponse page = getPage(initSearch.getPage(), totalCount);

		List<InquiryListResponse.Row> inquiries = new ArrayList<>();
		if (initSearch.getKeyword().isEmpty()) {
			for (Question question : qnADao.getQuestionsByPage((page.getPage() - 1) * page.getSize(), page.getSize())) {
				//날짜 포맷
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
				String formatDate = question.getCreatedDate().toLocalDateTime().format(formatter);

				//제목 변경
				String title = question.getTitle();

				//열람가능 여부
				boolean isOpen = String.valueOf(question.getOpen()).equalsIgnoreCase("y");
				if (!isOpen) {
					title = "비밀글입니다.";
				}

				inquiries.add(InquiryListResponse.Row.builder()
						.id(question.getId()).title(title).date(formatDate).writer(question.getMember().getNickName()).isOpen(isOpen)
						.answer(InquiryListResponse.Answer.builder().code(question.getAnswer()).text(AnswerStatus.match(question.getAnswer())).build())
						.build()
				);
			}
		} else {
			for (Question question : qnADao.getQuestionsBySearch(initSearch)) {
				//날짜 포맷
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
				String formatDate = question.getCreatedDate().toLocalDateTime().format(formatter);

				//제목 변경
				String title = question.getTitle();

				//열람가능 여부
				boolean isOpen = String.valueOf(question.getOpen()).equalsIgnoreCase("y");
				if (!isOpen) {
					title = "비밀글입니다.";
				}

				inquiries.add(InquiryListResponse.Row.builder()
						.id(question.getId()).title(title).date(formatDate).writer(question.getMember().getNickName()).isOpen(isOpen)
						.answer(InquiryListResponse.Answer.builder().code(question.getAnswer()).text(AnswerStatus.match(question.getAnswer())).build())
						.build());
			}
		}

		//검색 키워드 결과가 없는 상태
		String result = "";
		if (inquiries.isEmpty()) {
			if (initSearch.getMode().equalsIgnoreCase("period")) {
				result = "<span> 기간 내에 등록된 글이 없습니다. </span>";
			} else if (initSearch.getMode().equalsIgnoreCase("answer")) {
				result = "<span> 선택하신 답변상태에 해당하는 문의글이 없습니다. </span>";
			} else {
				result = "<span>" + initSearch.getKeyword().get(0) + "</span> 의 검색결과가 없습니다.";
			}
		}

		return InquiryListResponse.builder()
				.list(inquiries)
				.search(InquirySearchResponse.builder().mode(initSearch.getMode()).keyword(initSearch.getKeyword()).resultText(result).build())
				.page(page)
				.build();
	}


	/**
	 * 리스트에서 검색 조건을 초기화한 후 반환합니다.
	 *
	 * @param search 검색 정보
	 * @return 초기화된 객체
	 */
	private InquirySearchRequest initSearch(InquirySearchRequest search) {
		String mode = Objects.isNull(search.getMode()) ? "all" : search.getMode();
		List<String> keyword = Objects.isNull(search.getKeyword()) ? null : search.getKeyword();

		return new InquirySearchRequest(mode, keyword, initPage(search.getPage()));
	}


	/**
	 * 공지사항에 해당하는 페이징을 초기화 진행 후 반환합니다. <br>
	 * 현재 페이지와 한번에 표시할 공지사항 수가 없다면 서비스에서 설정한 기본 설정값으로 초기화됩니다.
	 *
	 * @param page 선택 페이지 정보
	 * @return 공지사항 정보
	 */
	private PageRequest initPage(PageRequest page) {
		int currentPage = Objects.isNull(page.getPage()) ? 1 : page.getPage();
		int size = Objects.isNull(page.getSize()) ? 10 : page.getSize();

		return new PageRequest(currentPage, size);
	}


	/**
	 * 공지사항 페이징을 반환합니다. <br>
	 *
	 * @param page       전달받은 페이지 객체
	 * @param totalCount 총 공지사항 개수
	 * @return 공지사항에 따른 페이징 처리
	 */
	private PageResponse getPage(PageRequest page, int totalCount) {
		int limit = 5;
		boolean isPrev = true, isNext = true;

		int end = (int) Math.ceil(page.getPage() / (double) limit) * limit;
		int start = end - (limit - 1);

		//마지막 페이지 설정
		int max = (int) Math.ceil(totalCount / (double) page.getSize()) == 0 ? 1 : (int) Math.ceil(totalCount / (double) page.getSize());
		if (end >= max) {
			end = max;
			isNext = false;
		}

		if (start == 1) {
			isPrev = false;
		}

		return PageResponse.builder().page(page.getPage()).size(page.getSize()).startPage(start).endPage(end).isPrev(isPrev).isNext(isNext).build();
	}


	public boolean isWriter(InquiryAccessRequest writer) {
		return qnADao.findMemberIdByQuestionId(writer.getId()).equals(writer.getMemberId());
	}


	/**
	 * 질문번호에 해당하는 질문과 답변을 가져옵니다.
	 *
	 * @param id 질문번호
	 * @return 질문과 답변을 반환
	 */
	public InquiryDetailResponse getQnA(Long id) {
		//질문정보 조회
		Question question = qnADao.findQuestionById(id);

		//질문날짜 포맷
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일 HH:ss");
		String formatQuestionDate = question.getCreatedDate().toLocalDateTime().format(formatter);

		if (String.valueOf(question.getAnswer()).equalsIgnoreCase("Y")) {
			Answer answer = qnADao.findAnswerByQuestionId(id);

			//답변날짜 포맷
			formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
			String formatAnswerDate = answer.getCreatedDate().toLocalDate().format(formatter);

			return InquiryDetailResponse.builder()
					.question(
							InquiryDetailResponse.QuestionDetail.builder()
									.date(formatQuestionDate).writer(question.getMember().getNickName())
									.questionDTO( QuestionDTO.builder().title(question.getTitle()).content(question.getContent()).build() )
									.build()
					)
					.answer(AnswerDTO.builder().content(answer.getContent()).date(formatAnswerDate).writer(answer.getAdmin().getNickName()).build())
					.build();
		} else {
			return InquiryDetailResponse.builder()
					.question(
							InquiryDetailResponse.QuestionDetail.builder()
									.date(formatQuestionDate).writer(question.getMember().getNickName())
									.questionDTO( QuestionDTO.builder().title(question.getTitle()).content(question.getContent()).build() )
									.build()
					)
					.build();
		}
	}


	public InquiryUpdateResponse getQuestionById(Long id) {
		Question question = qnADao.findQuestionById(id);

		boolean isOpen = String.valueOf(question.getOpen()).equalsIgnoreCase("y");

		return InquiryUpdateResponse.builder()
				.question( QuestionDTO.builder().title(question.getTitle()).isOpen(isOpen).content(question.getContent()).build() )
				.build();
	}


	public void deleteInquiry(String memberId, Long id) {

	}
}
