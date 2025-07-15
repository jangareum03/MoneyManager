package com.areum.moneymanager.service.main;

import com.areum.moneymanager.dao.main.QnADao;
import com.areum.moneymanager.dto.request.main.QnARequestDTO;
import com.areum.moneymanager.dto.request.main.SupportRequestDTO;
import com.areum.moneymanager.dto.response.main.QnAResponseDTO;
import com.areum.moneymanager.dto.response.main.SupportResponseDTO;
import com.areum.moneymanager.entity.Answer;
import com.areum.moneymanager.entity.Member;
import com.areum.moneymanager.entity.Question;
import com.areum.moneymanager.enums.type.AnswerStatus;
import com.areum.moneymanager.exception.ErrorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.areum.moneymanager.enums.ErrorCode.QUESTION_DELETE_UNKNOWN;


@Service
public class InquiryService {

	private final Logger logger = LogManager.getLogger(this);
	private final QnADao qnADao;

	public InquiryService(QnADao qnADao ) {
		this.qnADao = qnADao;
	}


	@Transactional
	public Long createQuestion( String memberId, QnARequestDTO.Create create ) {
		char open = Objects.isNull(create.getIsOpen()) ? 'Y' : create.getIsOpen() ? 'Y' : 'N';

		Question question = Question.builder()
				.member(Member.builder().id(memberId).build())
				.title( create.getTitle() ).content( create.getContent() ).open( open )
				.build();

		return qnADao.saveQuestion( question );
	}



	public Long updateQuestion( String memberId, QnARequestDTO.Update update ) {
		char open = Objects.isNull(update.getIsOpen()) ? 'Y' : update.getIsOpen() ? 'Y' : 'N';

		Question question = Question.builder()
				.member(Member.builder().id(memberId).build())
				.id(update.getId())
				.title( update.getTitle() ).content( update.getContent() ).open( open )
				.build();

		if( qnADao.updateQuestion( memberId, question ) ) {
			return question.getId();
		}

		return 0L;
	}


	public SupportResponseDTO.InquiryList getInquiriesByPage( SupportRequestDTO.Page pageReq, SupportRequestDTO.Search searchReq ) {
		SupportRequestDTO.Page initPage = initPage( pageReq );
		SupportRequestDTO.Search initSearch = initSearch(searchReq);

		int totalCount = qnADao.countQuestionBySearch( initSearch );
		SupportResponseDTO.Page page = getPage( initPage, totalCount );

		List<SupportResponseDTO.InquiryRow> inquiryRows = new ArrayList<>();
		for(Question question : qnADao.getQuestionsByPage( (initPage.getNum() - 1) * initPage.getSize(), initPage.getSize() )) {
			//날짜 포맷
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
			String formatDate = question.getCreatedDate().toLocalDateTime().format(formatter);

			//제목 변경
			String title = question.getTitle();

			//열람가능 여부
			boolean isOpen = String.valueOf( question.getOpen() ).equalsIgnoreCase("y");
			if( !isOpen ) {
				title = "비밀글입니다.";
			}

			inquiryRows.add( SupportResponseDTO.InquiryRow.builder().id(question.getId()).title(title).date(formatDate).writer(question.getMember().getNickName()).answer( SupportResponseDTO.Answer.builder().code(question.getAnswer()).text(AnswerStatus.match(question.getAnswer())).build() ).isOpen(isOpen).build() );
		}

		return SupportResponseDTO.InquiryList.builder().inquiries(inquiryRows).page(page).build();
	}



	public SupportResponseDTO.InquiryList getInquiriesBySearch( SupportRequestDTO.Page pageReq, SupportRequestDTO.Search searchReq ) {
		SupportRequestDTO.Page initPage = initPage( pageReq );
		SupportRequestDTO.Search initSearch = initSearch(searchReq);

		int totalCount = qnADao.countQuestionBySearch( initSearch );
		SupportResponseDTO.Page page = getPage( initPage, totalCount );

		List<SupportResponseDTO.InquiryRow> inquiryRows = new ArrayList<>();
		if( searchReq.getKeyword().isEmpty() ) {
			for(Question question : qnADao.getQuestionsByPage( (initPage.getNum() - 1) * initPage.getSize(), initPage.getSize() )) {
				//날짜 포맷
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
				String formatDate = question.getCreatedDate().toLocalDateTime().format(formatter);

				//제목 변경
				String title = question.getTitle();

				//열람가능 여부
				boolean isOpen = String.valueOf( question.getOpen() ).equalsIgnoreCase("y");
				if( !isOpen ) {
					title = "비밀글입니다.";
				}

				inquiryRows.add( SupportResponseDTO.InquiryRow.builder().id(question.getId()).title(title).date(formatDate).writer(question.getMember().getNickName()).answer( SupportResponseDTO.Answer.builder().code(question.getAnswer()).text(AnswerStatus.match(question.getAnswer())).build() ).isOpen(isOpen).build() );
			}
		}else {
			for(Question question : qnADao.getQuestionsBySearch( initSearch, (initPage.getNum() - 1) * initPage.getSize(), initPage.getSize() )) {
				//날짜 포맷
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
				String formatDate = question.getCreatedDate().toLocalDateTime().format(formatter);

				//제목 변경
				String title = question.getTitle();

				//열람가능 여부
				boolean isOpen = String.valueOf( question.getOpen() ).equalsIgnoreCase("y");
				if( !isOpen ) {
					title = "비밀글입니다.";
				}

				inquiryRows.add( SupportResponseDTO.InquiryRow.builder().id(question.getId()).title(title).date(formatDate).writer(question.getMember().getNickName()).answer( SupportResponseDTO.Answer.builder().code(question.getAnswer()).text(AnswerStatus.match(question.getAnswer())).build() ).isOpen(isOpen).build() );
			}
		}


		//검색 키워드 결과가 없는 상태
		String result = "";
		if( inquiryRows.isEmpty() ) {
			if( searchReq.getMode().equalsIgnoreCase("period") ) {
				result = "<span> 기간 내에 등록된 글이 없습니다. </span>";
			}else if( searchReq.getMode().equalsIgnoreCase("answer") ) {
				result = "<span> 선택하신 답변상태에 해당하는 문의글이 없습니다. </span>";
			}else {
				result = "<span>" + searchReq.getKeyword().get(0) + "</span> 의 검색결과가 없습니다.";
			}
		}

		return SupportResponseDTO.InquiryList.builder().resultText(result).inquiries(inquiryRows).page(page).build();
	}



	/**
	 * 리스트에서 검색 조건을 초기화한 후 반환합니다.
	 *
	 * @param search	검색 정보
	 * @return	초기화된 객체
	 */
	private SupportRequestDTO.Search initSearch( SupportRequestDTO.Search search ) {
		String mode = Objects.isNull(search.getMode()) ? "all" : search.getMode();
		List<String> keyword = Objects.isNull(search.getKeyword()) ? null : search.getKeyword();

		return SupportRequestDTO.Search.builder().mode(mode).keyword(keyword).build();
	}



	/**
	 * 공지사항에 해당하는 페이징을 초기화 진행 후 반환합니다. <br>
	 * 현재 페이지와 한번에 표시할 공지사항 수가 없다면 서비스에서 설정한 기본 설정값으로 초기화됩니다.
	 *
	 * @param page			선택 페이지 정보
	 * @return	공지사항 정보
	 */
	private SupportRequestDTO.Page initPage( SupportRequestDTO.Page page ) {
		int currentPage = Objects.isNull(page.getNum()) ? 1 : page.getNum();
		int size = Objects.isNull(page.getSize()) ? 10: page.getSize();

		return SupportRequestDTO.Page.builder().num(currentPage).size(size).build();
	}



	/**
	 * 공지사항 페이징을 반환합니다. <br>
	 *
	 * @param page									전달받은 페이지 객체
	 * @param totalCount				총 공지사항 개수
	 * @return	공지사항에 따른 페이징 처리
	 */
	private SupportResponseDTO.Page getPage(SupportRequestDTO.Page page, int totalCount ) {
		int limit = 5;
		boolean isPrev = true, isNext = true;

		int end = (int) Math.ceil( page.getNum() / (double)limit ) * limit;
		int start = end - (limit - 1);

		//마지막 페이지 설정
		int max = (int)Math.ceil( totalCount / (double) page.getSize() ) == 0 ? 1 : (int)Math.ceil( totalCount / (double) page.getSize() );
		if( end >= max  ) {
			end = max;
			isNext = false;
		}

		if( start == 1 ) {
			isPrev = false;
		}

		return SupportResponseDTO.Page.builder().page( page.getNum() ).size( page.getSize() ).start( start ).end( end ).isPrev( isPrev ).isNext( isNext ).build();
	}



	public boolean isWriter( QnARequestDTO.CheckWriter writer ) {
		return qnADao.findMemberIdByQuestionId(writer.getId()).equals(writer.getMemberId());
	}



	/**
	 * 질문번호에 해당하는 질문과 답변을 가져옵니다.
	 *
	 * @param id		질문번호
	 * @return	질문과 답변을 반환
	 */
	public QnAResponseDTO.Detail getQnA( Long id ) {
		//질문정보 조회
		Question question = qnADao.findQuestionById( id );

		//질문날짜 포맷
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일 HH:ss");
		String formatQuestionDate = question.getCreatedDate().toLocalDateTime().format(formatter);

		if( String.valueOf(question.getAnswer()).equalsIgnoreCase("Y") ) {
			Answer answer = qnADao.findAnswerByQuestionId( id );

			//답변날짜 포맷
			formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
			String formatAnswerDate = answer.getCreatedDate().toLocalDate().format(formatter);

			return QnAResponseDTO.Detail.builder()
					.question( QnAResponseDTO.Question.builder()
							.title(question.getTitle()).content(question.getContent())
							.date(formatQuestionDate).writer(question.getMember().getNickName())
							.build() )
					.answer( QnAResponseDTO.Answer.builder().content(answer.getContent()).date(formatAnswerDate).writer(answer.getAdmin().getNickName()).build() )
					.build();
		}else {
			return QnAResponseDTO.Detail.builder()
					.question( QnAResponseDTO.Question.builder()
							.title(question.getTitle()).content(question.getContent())
							.date(formatQuestionDate).writer(question.getMember().getNickName())
							.build() )
					.answer( null )
					.build();
		}
	}


	public QnAResponseDTO.Update getQuestionById( Long id ) {
		Question question = qnADao.findQuestionById(id);

		boolean isOpen = String.valueOf( question.getOpen() ).equalsIgnoreCase("y");

		return QnAResponseDTO.Update.builder()
				.title(question.getTitle()).isOpen(isOpen).content(question.getContent()).build();
	}



	public void deleteInquiry( String memberId, Long id ) {
		if( !qnADao.deleteQuestion(memberId, id) ) {
			throw new ErrorException( QUESTION_DELETE_UNKNOWN );
		}

	}
}
